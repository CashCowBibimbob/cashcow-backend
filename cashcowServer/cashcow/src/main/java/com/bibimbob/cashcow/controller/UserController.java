package com.bibimbob.cashcow.controller;

import com.bibimbob.cashcow.domain.User;
import com.bibimbob.cashcow.dto.StockDto.PageInfo;
import com.bibimbob.cashcow.dto.StockDto.ResponsePagingStockDto;

import com.bibimbob.cashcow.dto.StockDto.ResponseStockDto;
import com.bibimbob.cashcow.dto.StockDto.UserStockDto;

import com.bibimbob.cashcow.dto.User.UserDto;
import com.bibimbob.cashcow.dto.User.UserRequestDto.DeleteUserDto;
import com.bibimbob.cashcow.dto.User.UserRequestDto.RequestPwMatchDto;
import com.bibimbob.cashcow.dto.User.UserRequestDto.RequestUpdatePwDto;
import com.bibimbob.cashcow.dto.User.UserResponseDto.ResponseCheckDto;
import com.bibimbob.cashcow.dto.User.UserResponseDto.ResponsePwMatchDto;
import com.bibimbob.cashcow.dto.User.UserResponseDto.ResponseSaveDto;
import com.bibimbob.cashcow.repository.UserJpaRepository;
import com.bibimbob.cashcow.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@Api(tags = {"유저 API"})
@RequiredArgsConstructor
@CrossOrigin(origins="*", methods={RequestMethod.GET, RequestMethod.POST})
public class UserController {

    private final UserService userService;
    private final UserJpaRepository userJpaRepository;

    /**
     * 회원가입 (회원 DB에 SAVE)
     */

    @ApiOperation(value = "회원 가입", notes = "유저 정보를 입력받아 회원 DB에 저장하는 API입니다.")
    @PostMapping("/join")
    public ResponseSaveDto join(@RequestBody UserDto userDto) throws Exception{

        // dto -> entity
        User user = userDto.toEntity();

        // save
        userService.save(user);
        return new ResponseSaveDto(user.getId());
    }

    /**
     * id로 회원 찾기
     */

    @ApiOperation(value = "회원 조회", notes = "유저 pk로 회원을 조회하는 API입니다.")
    @GetMapping("/getUser")
    public UserDto getUser(Long id) throws Exception{
        User user = userService.findOne(id);
        return new UserDto(user);
    }


    /**
     * 유저 정보 update (비밀번호 제외)
     */
    @ApiOperation(value = "회원 정보 업데이트", notes = "해당 회원의 정보를 수정하는 API입니다.")
    @PostMapping("/updateUser")
    public StatusResponse updateUser(@RequestBody UserDto userDto, Exception e) throws Exception{
        // DB에 UPDATE
        userService.updateUser(userDto);
        return new StatusResponse(HttpStatus.OK);
    }

    /**
     * 비밀번호 update
     */
    @ApiOperation(value = "회원 비밀번호 업데이트", notes = "해당 회원의 비밀번호를 수정하는 API입니다.")
    @PostMapping("/updateUserPw")
    public StatusResponse updateUserPw(@RequestBody RequestUpdatePwDto requestUpdatePwDto) throws Exception{
        // DB에 UPDATE
        userService.updatePw(requestUpdatePwDto.getId(), requestUpdatePwDto.getUserPass());
        return new StatusResponse(HttpStatus.OK);
    }


    /**
     * 아이디 중복 체크
     */
    @ApiOperation(value = "회원가입 아이디 중복 체크", notes = "회원가입시 아이디 중복 여부 확인하는 API입니다.")
    @GetMapping("/idCheck")
    public ResponseCheckDto idCheck(String userId) throws Exception{
        return new ResponseCheckDto(userService.findById(userId));
    }

    /**
    * 로그인 용 비밀번호 매칭
     */
    @ApiOperation(value = "비밀번호 매치", notes = "로그인시 비밀번호 확인하는 API입니다.")
    @PostMapping("/passwordMatch")
    public ResponsePwMatchDto passwordMatch(@RequestBody RequestPwMatchDto requestPwMatchDto) throws Exception{

        Optional<User> findUser = userJpaRepository.findByUserId(requestPwMatchDto.getUserId());

        if(findUser.isPresent()){
            // 비밀번호 매치
            boolean result = userService.passwordMatch(requestPwMatchDto.getUserId(), requestPwMatchDto.getUserPass());
            return new ResponsePwMatchDto(true,findUser.get().getId(),result);
        }else{
            return new ResponsePwMatchDto(false,0,false);
        }

    }

    /**
     * 회원 탈퇴
     */
    @ApiOperation(value = "회원 탈퇴", notes = "해당 회원의 정보를 삭제하는 API입니다.")
    @PostMapping("/deleteUser")
    public StatusResponse deleteUser(@RequestBody DeleteUserDto deleteUserDto) throws Exception{
        // DB에 REMOVE
        userService.deleteUser(deleteUserDto.getUserId());
        return new StatusResponse(HttpStatus.OK);
    }

    /**
     *  즐겨찾기 주식 저장
     */
    @ApiOperation(value = "회원 주식 즐겨찾기 저장", notes = "해당 회원의 주식 즐겨찾기 저장하는 API입니다.")
    @PostMapping("/saveStock")
    public StatusResponse saveStock(@RequestBody UserStockDto userStockDto) throws Exception{
        // DB에 INSERT
        userService.saveStock(userStockDto);
        return new StatusResponse(HttpStatus.OK);
    }

    /**
     * 즐겨찾기 주식 삭제
     */
    @ApiOperation(value = "회원 주식 즐겨찾기 삭제", notes = "해당 회원의 주식 즐겨찾기 삭제하는 API입니다.")
    @PostMapping("/removeStock")
    public StatusResponse removeStock(@RequestBody UserStockDto userStockDto) throws Exception{
        // DB에 REMOVE
        userService.removeStock(userStockDto);
        return new StatusResponse(HttpStatus.OK);
    }

    /**
     * 즐겨찾기 주식 목록 GET
     */
    @ApiOperation(value = "회원 주식 즐겨찾기 목록 조회", notes = "해당 회원의 주식 즐겨찾기 목록을 조회하는 API입니다.")
    @GetMapping("/getStock")
    public ResponsePagingStockDto getStock(Pageable pageable, Long id) throws Exception{

        // DB에서 get해서 paging
        PageInfo pageInfo = new PageInfo(pageable.getPageNumber(),
                userService.getStockList(pageable, id).getTotalElements(),
                pageable.getPageSize());

        return new ResponsePagingStockDto(userService.getStockList(pageable,id).map(s -> new ResponseStockDto(s.getStockCode())).toList(), pageInfo);
    }


    @Getter
    static class StatusResponse{
        private int status;

        public StatusResponse(HttpStatus status) {
            this.status = status.value();
        }
    }


}
