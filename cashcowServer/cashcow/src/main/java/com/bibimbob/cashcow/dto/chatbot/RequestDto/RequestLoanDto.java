package com.bibimbob.cashcow.dto.chatbot.RequestDto;

import com.bibimbob.cashcow.dto.User.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLoanDto {
    private int creditScore; // 유저 신용 점수
    private long loanAmount;
    private UserDto userDto;
}
