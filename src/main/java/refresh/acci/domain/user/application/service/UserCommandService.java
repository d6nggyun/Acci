package refresh.acci.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import refresh.acci.domain.user.infra.persistence.UserRepository;
import refresh.acci.domain.user.model.User;
import refresh.acci.global.exception.CustomException;
import refresh.acci.global.exception.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다. userId: {}", userId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        user.softDelete();
        log.info("회원 탈퇴 완료 - userId: {}, providerId: {}", userId, user.getProviderId());
    }
}
