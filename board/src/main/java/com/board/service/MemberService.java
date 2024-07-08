package com.board.service;

import java.util.List;
import com.board.dto.AddressVO;
import com.board.dto.MemberVO;

public interface MemberService {

	//아이디 확인
	public int idCheck(String userid); 

	//로그인 정보 확인
	public MemberVO login(String userid); 
	
	//마지막 로드인 시간 등록
	public void logindateUpdate(String userid);
	
	//welcome 페이지 정보 가져 오기 
	public MemberVO welcomeView(String userid);

	//로그아웃 날짜 업데이트
	public void logoutUpdate(String userid);

	//사용자 정보 등록
	public void memberInfoRegistry(MemberVO member);
	
	//사용자 정보 보기
	public MemberVO memberInfoView(String userid);
	
	//사용자 정보 수정
	public void memberInfoUpdate(MemberVO member);
	
	//패스워드 수정
	public void passwordUpdate(MemberVO member);
	
	//사용자 아이디 찾기
	public String searchID(MemberVO member);
	
	//사용자 패스워드 신규 발급을 위한 확인
	public int searchPassword(MemberVO member);
	
	//회원 탈퇴
	public void memberInfoDelete(String userid);
	
	//주소 전체 갯수 계산
	public int addrTotalCount(String addrSearch);
	
	//주소 검색
	public List<AddressVO> addrSearch(int startPoint, int endPoint, String addrSearch);
	
	
}
