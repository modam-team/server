package com.example.modam.domain.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean checkNicknameDuplication(String nickname){
        return userRepository.existsByNickname(nickname);
    }

}
