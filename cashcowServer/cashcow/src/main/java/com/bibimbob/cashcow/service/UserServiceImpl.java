package com.bibimbob.cashcow.service;

import com.bibimbob.cashcow.domain.User;
import com.bibimbob.cashcow.dto.UserDto;
import com.bibimbob.cashcow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User save(UserDto userDto) throws Exception {
        User savedUser = userRepository.save(User.builder()
                        .id(userDto.getId())
                        .name(userDto.getName())
                        .password(userDto.getPassword())
                        .nickname(userDto.getNickname())
                        .gender(userDto.getGender())
                        .job(userDto.getJob())
                        .status(userDto.getStatus())
                        .createdAt(userDto.getCreatedAt())
                        .modifiedAt(userDto.getModifiedAt())
                        .phoneNumber(userDto.getPhoneNumber())
                        .accessToken(userDto.getAccessToken())
                        .refreshToken(userDto.getRefreshToken())
                        .userseqno(userDto.getUserseqno()).build());
        return savedUser;
    }

    @Override
    public UserDto getUser(long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(()->
        new Exception("해당 사용자가 존재하지 않습니다. : "+id));
        UserDto userDto = new UserDto(user);
        return userDto;
    }


}