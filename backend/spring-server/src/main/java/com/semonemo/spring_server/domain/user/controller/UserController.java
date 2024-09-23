package com.semonemo.spring_server.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semonemo.spring_server.domain.user.dto.response.UserInfoResponseDTO;
import com.semonemo.spring_server.domain.user.entity.Users;
import com.semonemo.spring_server.domain.user.service.UserService;
import com.semonemo.spring_server.global.common.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController implements UserApi {

	private final UserService userService;

	@Override
	@GetMapping("/me")
	public CommonResponse<UserInfoResponseDTO> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
		Users user = userService.findByAddress(userDetails.getUsername());
		UserInfoResponseDTO responseDTO = UserInfoResponseDTO.fromEntity(user);
		return CommonResponse.success(responseDTO, "사용자 정보 조회에 성공했습니다.");
	}

	@Override
	@GetMapping("/{userId}/detail")
	public CommonResponse<UserInfoResponseDTO> getUserInfo(@PathVariable long userId) {
		Users user = userService.findById(userId);
		UserInfoResponseDTO responseDTO = UserInfoResponseDTO.fromEntity(user);
		return CommonResponse.success(responseDTO, "사용자 정보 조회에 성공했습니다.");
	}
}