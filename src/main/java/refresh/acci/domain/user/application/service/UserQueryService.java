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
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다. userId: {}", userId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
    }
}



