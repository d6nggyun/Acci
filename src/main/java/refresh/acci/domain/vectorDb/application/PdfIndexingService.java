package refresh.acci.domain.vectorDb.application;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import refresh.acci.domain.vectorDb.infra.PgVectorChunkRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfIndexingService {

    private final PgVectorChunkRepository pgVectorChunkRepository;
    private final GeminiEmbeddingService geminiEmbeddingService;
    private final PageToAccidentTypeMapper pageToAccidentTypeMapper;

    private static final List<String> LAW_HEADERS = List.of("관련 법규", "관련법규", "도로교통법");
    private static final List<String> PRECEDENT_HEADERS = List.of("참고 판례", "참고판례");

    public void indexPdf(Path pdfPath, String docName, int startPage, int endPage) throws IOException {
        if (startPage == 1) {
            pgVectorChunkRepository.deleteByDocName(docName);
        }

        try (PDDocument doc = PDDocument.load(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            int pages = doc.getNumberOfPages();
            int from = Math.max(1, startPage);
            int to = Math.min(pages, endPage);

            for (int page = from; page <= to; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String pageText = stripper.getText(doc).trim();
                if (pageText.isBlank()) continue;

                Integer mappedType = pageToAccidentTypeMapper.findTypeByPage(page);
                if (mappedType == null) continue;

                List<SectionBlock> blocks = splitBySection(pageText);

                for (SectionBlock block : blocks) {
                    // 섹션 텍스트가 너무 짧으면 skip(선택)
                    if (block.text().isBlank() || block.text().length() < 30) continue;

                    for (String chunk : chunk(block.text(), 2000, 250)) {
                        float[] emb = geminiEmbeddingService.embed(chunk);

                        pgVectorChunkRepository.insertChunk(
                                mappedType,
                                docName,
                                page,
                                block.section(),
                                null,
                                chunk,
                                emb
                        );

                        Thread.sleep(1000);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record SectionBlock(String section, String text) {}

    /**
     * 페이지 텍스트를 MAIN/LAW/PRECEDENT 구간으로 분리
     * "헤더 위치" 기준으로 나누기
     */
    private List<SectionBlock> splitBySection(String pageText) {
        String normalized = pageText.replace("\r", "");

        int lawIdx = findFirstIndex(normalized, LAW_HEADERS);
        int precIdx = findFirstIndex(normalized, PRECEDENT_HEADERS);

        // 둘 다 없으면 MAIN만
        if (lawIdx < 0 && precIdx < 0) {
            return List.of(new SectionBlock("MAIN", normalized));
        }

        // 구간을 시작 인덱스 기준으로 정렬
        // (MAIN -> LAW -> PRECEDENT 순서가 보장되지 않을 수 있어서 안전하게 처리)
        List<Marker> markers = new ArrayList<>();
        if (lawIdx >= 0) markers.add(new Marker("LAW", lawIdx));
        if (precIdx >= 0) markers.add(new Marker("PRECEDENT", precIdx));
        markers.sort((a, b) -> Integer.compare(a.idx, b.idx));

        List<SectionBlock> blocks = new ArrayList<>();

        // 첫 marker 앞은 MAIN
        int first = markers.get(0).idx;
        if (first > 0) {
            blocks.add(new SectionBlock("MAIN", normalized.substring(0, first).trim()));
        }

        // marker 구간들
        for (int i = 0; i < markers.size(); i++) {
            int start = markers.get(i).idx;
            int end = (i + 1 < markers.size()) ? markers.get(i + 1).idx : normalized.length();
            String sec = markers.get(i).section;
            blocks.add(new SectionBlock(sec, normalized.substring(start, end).trim()));
        }

        return blocks;
    }

    private static class Marker {
        String section;
        int idx;
        Marker(String section, int idx) {
            this.section = section;
            this.idx = idx;
        }
    }

    private int findFirstIndex(String text, List<String> keywords) {
        int best = -1;
        for (String k : keywords) {
            int idx = text.indexOf(k);
            if (idx >= 0 && (best == -1 || idx < best)) best = idx;
        }
        return best;
    }

    private List<String> chunk(String text, int size, int overlap) {
        List<String> out = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + size);
            out.add(text.substring(start, end));
            if (end == text.length()) break;
            start = end - overlap;
            if (start < 0) start = 0;
        }
        return out;
    }
}
