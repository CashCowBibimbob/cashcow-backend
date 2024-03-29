package com.bibimbob.cashcow.controller;

import com.bibimbob.cashcow.dto.User.UserDto;
import com.bibimbob.cashcow.dto.chatbot.RequestDto.RequestDepositDto;
import com.bibimbob.cashcow.dto.chatbot.RequestDto.RequestLoanDto;
import com.bibimbob.cashcow.dto.chatbot.RequestDto.RequestSavingDto;
import com.bibimbob.cashcow.dto.chatbot.ResponseDto.*;
import com.bibimbob.cashcow.dto.chatbot.RequestDto.RequestDto;
import com.bibimbob.cashcow.dto.chatbot.UserAssetsDto.DepositDto;
import com.bibimbob.cashcow.dto.chatbot.UserAssetsDto.LoanDto;
import com.bibimbob.cashcow.dto.chatbot.UserAssetsDto.SavingDto;
import com.bibimbob.cashcow.feign.DialogFlowFeign;
import com.bibimbob.cashcow.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = {"챗봇 API"})
@RequiredArgsConstructor
@CrossOrigin(origins="*", methods={RequestMethod.GET, RequestMethod.POST})
public class ChatbotController {

    private final UserService userService;
    private final DialogFlowFeign dialogFlowFeign;

    /**
     *  유저가 string 으로 챗봇 요청
     */
    @ApiOperation(value = "회원 챗봇 요청", notes = "챗봇 요청 메시지를 dialog-flow 서버에 보내는 API입니다.")
    @PostMapping("/chatbot/request")
    public ResponseDialogFlowDto chatbotRequest(@RequestBody RequestDto requestDto) throws Exception {

        // dialog server에 post 요청
        DialogFlowDto response=dialogFlowFeign.dialog_flow(requestDto);
        return new ResponseDialogFlowDto(response.getTermsDescription(), response.getFulfillmentText(), response.getIntent());
    }

    /**
     * 예금 정보 입력 후 DIALOG-FLOW
     */
    @ApiOperation(value = "회원 예금 정보 입력", notes = "해당 회원의 예금 정보를 받는 API입니다.")
    @PostMapping("/chatbot/deposit")
    public ReturnDto requestDeposit( @RequestBody DepositDto depositDto) throws Exception {


        // 유저 찾기 -> DTO 에 담기
        UserDto userDto = new UserDto(userService.findOne(depositDto.getId()));

        // 예금 정보 + 유저 정보 합쳐 request 위한 dto 생성
        RequestDepositDto requestDepositDto = new RequestDepositDto(depositDto.getExpectedPeriod(), depositDto.getSavingAmount(), userDto);

        // feign 인터페이스로 POST 요청
        return new ReturnDto(dialogFlowFeign.deposit_products_search(requestDepositDto).stream().map(s -> new ResponseDepositDto(
                        new ResponseProductInfo(s.getProductInfo()),
                        s.getEstimatedSaving()))
                .collect(Collectors.toList()));
    }

    /**
     * 적금 정보 입력 후 DIALOG-FLOW
     */
    @ApiOperation(value = "회원 적금 정보 입력", notes = "해당 회원의 적금 정보를 받는 API입니다.")
    @PostMapping("/chatbot/saving")
    public ReturnDto requestSaving(@RequestBody SavingDto savingDto) throws Exception {

        // 유저 찾기 -> DTO 에 담기
        UserDto userDto = new UserDto(userService.findOne(savingDto.getId()));

        // 적금 정보 + 유저 정보 합쳐 request 위한 dto 생성
        RequestSavingDto requestSavingDto = new RequestSavingDto(savingDto.getExpectedPeriod(), savingDto.getSavingAmount(), savingDto.getMonthlyPayment(), userDto);

        // dialog server POST 요청
        return new ReturnDto(dialogFlowFeign.saving_products_search(requestSavingDto).stream().map(s -> new ResponseDepositDto(
                        new ResponseProductInfo(s.getProductInfo()),
                        s.getEstimatedSaving()))
                .collect(Collectors.toList()));
    }

    /**
     * 대출 정보 입력 후 DIALOG-FLOW
     */
    @ApiOperation(value = "회원 대출 정보 입력", notes = "해당 회원의 대출 정보를 받는 API입니다.")
    @PostMapping("/chatbot/loan")
    public ReturnDto requestLoan( @RequestBody LoanDto loanDto) throws Exception {

        // 유저 찾기 -> DTO 에 담기
        UserDto userDto = new UserDto(userService.findOne(loanDto.getId()));

        // 적금 정보 + 유저 정보 합쳐 request 위한 dto 생성
        RequestLoanDto requestLoanDto = new RequestLoanDto(loanDto.getCreditScore(), loanDto.getLoanAmount(), userDto);

        // dialog server POST 요청
        return new ReturnDto(dialogFlowFeign.credit_loan_products_search(requestLoanDto).stream()
                .map(s -> new ResponseLoanDto(
                        new ResponseLoanProductInfo(s.getLoanProductInfo())))
                .collect(Collectors.toList()));
    }


    @Data
    @AllArgsConstructor
    static class ReturnDto<T> {
//        private ChatResponse ChatEnum;
        private T data;
    }


}
