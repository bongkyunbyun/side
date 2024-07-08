package com.board.controller;

import java.io.File;import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.board.dto.AddressVO;
import com.board.dto.FileVO;
import com.board.dto.MemberVO;
import com.board.service.BoardService;
import com.board.service.MemberService;
import com.board.util.Page;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MemberController {

	private final MemberService service;
	private final BoardService boardService;
	private final BCryptPasswordEncoder pwdEncoder;
	
	//�궗�슜�옄 �벑濡� �솕硫� 蹂닿린
	@RequestMapping(value="/member/signup",method=RequestMethod.GET)
	public void getMemberRegistry() throws Exception { }
	
	//�궗�슜�옄 �벑濡� 泥섎━
	@RequestMapping(value="/member/signup",method=RequestMethod.POST)
	public String postMemberRegistry(MemberVO member,
			@RequestParam("fileUpload") MultipartFile multipartFile ) {
		
		String path = "c:\\Repository\\profile\\";
		File targetFile;
		
		if(!multipartFile.isEmpty()) {
				
				String org_filename = multipartFile.getOriginalFilename();	
				String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
				String stored_filename =  UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
								
				try {
					targetFile = new File(path + stored_filename);
					
					multipartFile.transferTo(targetFile);
					
					member.setOrg_filename(org_filename);
					member.setStored_filename(stored_filename);
					member.setFilesize(multipartFile.getSize());
																				
				} catch (Exception e ) { e.printStackTrace(); }
				
				String inputPassword = member.getPassword();
				String pwd = pwdEncoder.encode(inputPassword); 
				member.setPassword(pwd);			
				
		}	

		service.memberInfoRegistry(member);
		return "redirect:/";
	}
	
	//�궗�슜�옄 �벑濡� �떆 �븘�씠�뵒 以묐났 �솗�씤
	@ResponseBody //Ajax �옄諛붿뒪�겕由쏀듃 �븿�닔�� 媛믪쓣 鍮꾨룞湲곕줈 援먰솚�븯�뒗 硫붿냼�뱶 �븵�뿉�뒗 諛섎뱶�떆 @ResponseBody
	@RequestMapping(value="/member/idCheck",method=RequestMethod.POST)
	public int idCheck(@RequestParam("userid") String userid) throws Exception{
		
		//0,1 媛믪씠 �뱾�뼱 �삩�떎
		int result = service.idCheck(userid);
		
		return result;
	}
	
	//濡쒓렇�씤 �솕硫� 蹂닿린
	@RequestMapping(value="/member/login",method=RequestMethod.GET)
	public void getLogIn() { }
	
	//濡쒓렇�씤 泥섎━
	@RequestMapping(value="/member/login",method=RequestMethod.POST)
	public String postLogIn(MemberVO loginData,Model model,HttpSession session,
			RedirectAttributes rttr) {
		
		//�븘�씠�뵒 議댁옱 �뿬遺� �솗�씤
		if(service.idCheck(loginData.getUserid()) == 0) {
			rttr.addFlashAttribute("message", "ID_NOT_FOUND");
			return "redirect:/";
		}
		
		MemberVO member = service.login(loginData.getUserid());
		
		//�뙣�뒪�썙�뱶 �솗�씤
		if(!pwdEncoder.matches(loginData.getPassword(),member.getPassword())) {
			rttr.addFlashAttribute("message", "PASSWORD_NOT_FOUND");
			return "redirect:/";
		}else {
		
		//濡쒓렇�씤 �궇吏� �벑濡�
		service.logindateUpdate(loginData.getUserid());
		
		//�꽭�뀡 �깮�꽦
		//�꽭�뀡 �쑀吏� �떆媛� �꽕�젙 --> 珥� �떒�쐞
		session.setMaxInactiveInterval(3600*7);
		session.setAttribute("userid", member.getUserid());
		session.setAttribute("username", member.getUsername());
		session.setAttribute("role", member.getRole());

		// <------------------- 怨쇱젣 ------------------------> 
		//�뙣�뒪�썙�뱶蹂�寃쎌씪�씠 30�씪�씠 寃쎄낵�릺�뿀�뒗吏� �솗�씤 �썑 由щ떎�씠�젆�듃濡� welcome �삉�뒗 �뙣�뒪�썙�뱶 蹂�寃� �럹�씠吏�濡� �씠�룞
		
		return "redirect:/userManage/welcome";
		}

	}
	
	//welcome �럹�씠吏� �젙蹂� 媛��졇 �삤湲�
	@RequestMapping(value="/userManage/welcome",method=RequestMethod.GET)
	public void getWelcomeView(HttpSession session,Model model) {
		
		String userid = (String)session.getAttribute("userid");
		String username = (String)session.getAttribute("username");
		
		MemberVO member = service.welcomeView(userid);
		
		model.addAttribute("userid", userid);
		model.addAttribute("username", username);
		model.addAttribute("regdate", member.getRegdate());
		model.addAttribute("lastlogindate", member.getLastlogindate());
		model.addAttribute("lastlogoutdate", member.getLastlogoutdate());
		
	}
	
	//濡쒓렇�븘�썐
	@RequestMapping(value="/userManage/logout",method=RequestMethod.GET)
	public void getLogout(HttpSession session,Model model) {
		
		String userid = (String)session.getAttribute("userid");
		String username = (String)session.getAttribute("username");

		//濡쒓렇 �븘�썐 �궇吏� �벑濡�
		service.logoutUpdate(userid);
		
		model.addAttribute("userid", userid);
		model.addAttribute("username", username);
		
		//紐⑤뱺 �꽭�뀡媛� �궘�젣 --> 濡쒓렇�븘�썐...
		session.invalidate(); 
		
	}

	//�궗�슜�옄 �젙蹂� 蹂닿린
	@RequestMapping(value="/userManage/memberInfo",method=RequestMethod.GET)
	public void gerMemberInfoView(Model model,HttpSession session) {
		
		String userid = (String)session.getAttribute("userid");
		MemberVO member = service.memberInfoView(userid);
		MemberVO member_date = service.welcomeView(userid);
		
		model.addAttribute("member", member);
		model.addAttribute("member_date", member_date);
		
	}
	
	//�궗�슜�옄 �젙蹂� �닔�젙 蹂닿린
	@RequestMapping(value="/userManage/memberInfoModify",method=RequestMethod.GET)
	public void getMemberInfoModify(Model model,HttpSession session) {
		
		String userid = (String)session.getAttribute("userid");
		MemberVO member = service.memberInfoView(userid);
		MemberVO member_date = service.welcomeView(userid);
		
		model.addAttribute("member", member);
		model.addAttribute("member_date", member_date);
		
	}
	
	//�궗�슜�옄 �젙蹂� �닔�젙
	@RequestMapping(value="/userManager/memberInfoModify",method=RequestMethod.POST)
	public String postMemberInfoModify(MemberVO member,
			@RequestParam("fileUpload") MultipartFile multipartFile ) {
	
	String path = "c:\\Repository\\profile\\";
	File targetFile;
	
	if(!multipartFile.isEmpty()) {

		//湲곗〈 �봽濡쒗뙆�씪 �씠誘몄� �궘�젣
		MemberVO vo = new MemberVO();
		vo = service.memberInfoView(member.getUserid());
		File file = new File(path + vo.getStored_filename());
		file.delete();
		
		String org_filename = multipartFile.getOriginalFilename();	
		String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));	
		String stored_filename =  UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
						
		try {
			targetFile = new File(path + stored_filename);
			
			multipartFile.transferTo(targetFile);
			
			member.setOrg_filename(org_filename);
			member.setStored_filename(stored_filename);
			member.setFilesize(multipartFile.getSize());
																		
		} catch (Exception e ) { e.printStackTrace(); }
			
	}	

	service.memberInfoUpdate(member);
	return "redirect:/userManage/memberInfo";
		
	}
	
	//�궗�슜�옄 �뙣�뒪�썙�뱶 蹂�寃� 蹂닿린
	@RequestMapping(value="/userManage/memberPasswordModify",method=RequestMethod.GET)
	public void getMemberPasswordModify() { }
	
	//�궗�슜�옄 �뙣�뒪�썙�뱶 蹂�寃� 
	@RequestMapping(value="/userManage/memberPasswordModify",method=RequestMethod.POST)
	public String postMemberPasswordModify(@RequestParam("old_userpassword") String old_password,
			@RequestParam("new_userpassword") String new_password, HttpSession session) { 
		
		String userid = (String)session.getAttribute("userid");
		
		MemberVO member = service.memberInfoView(userid);
		if(pwdEncoder.matches(old_password, member.getPassword())) {
			member.setPassword(pwdEncoder.encode(new_password));
			service.passwordUpdate(member);
		}	
		return "redirect:/userManage/logout";
	}
	
	//�궗�슜�옄 �븘�씠�뵒 李얘린 蹂닿린
	@RequestMapping(value="/member/searchID",method=RequestMethod.GET)
	public void getSearchID() { } 
	
	//�궗�슜�옄 �븘�씠�뵒 李얘린 
	@RequestMapping(value="/member/searchID",method=RequestMethod.POST)
	public String postSearchID(MemberVO member, RedirectAttributes rttr) { 
		
		String userid = service.searchID(member);
				
		//議곌굔�뿉 �빐�떦�븯�뒗 �궗�슜�옄媛� �븘�땺 寃쎌슦 
		if(userid == null ) { 
			rttr.addFlashAttribute("msg", "ID_NOT_FOUND");
			return "redirect:/member/searchID"; 
		}
		
		return "redirect:/member/IDView?userid=" + userid;		
	} 

	//李얠� �븘�씠�뵒 蹂닿린
	@RequestMapping(value="/member/IDView",method=RequestMethod.GET)
	public void postSearchID(@RequestParam("userid") String userid, Model model) {
		
		model.addAttribute("userid", userid);
		
	}
	
	//�궗�슜�옄 �뙣�뒪�썙�뱶 �엫�떆 諛쒓툒 蹂닿린
	@RequestMapping(value="/member/searchPassword",method=RequestMethod.GET)
	public void getSearchPassword() { } 
	
	
	//�궗�슜�옄 �뙣�뒪�썙�뱶 �엫�떆 諛쒓툒
	@RequestMapping(value="/member/searchPassword",method=RequestMethod.POST)
	public String postSearchPassword(MemberVO member, RedirectAttributes rttr) { 
		
		if(service.searchPassword(member)==0) {
			
			rttr.addFlashAttribute("msg", "PASSWORD_NOT_FOUND");
			return "redirect:/member/searchPassword"; 
			
		}
		
		//�닽�옄 + �쁺臾몃��냼臾몄옄 7�옄由� �엫�떆�뙣�뒪�썙�뱶 �깮�꽦
		StringBuffer tempPW = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 7; i++) {
		    int rIndex = rnd.nextInt(3);
		    switch (rIndex) {
		    case 0:
		        // a-z : �븘�뒪�궎肄붾뱶 97~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 97));
		        break;
		    case 1:
		        // A-Z : �븘�뒪�궎肄붾뱶 65~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 65));
		        break;
		    case 2:
		        // 0-9
		    	tempPW.append((rnd.nextInt(10)));
		        break;
		    }
		}
		
		member.setPassword(pwdEncoder.encode(tempPW));
		service.passwordUpdate(member);
			
		return "redirect:/member/tempPWView?password=" + tempPW;
		
	} 
	
	//諛쒓툒�븳 �엫�떆�뙣�뒪�썙�뱶 蹂닿린
	@RequestMapping(value="/member/tempPWView",method=RequestMethod.GET)
	public void getTempPWView(Model model, @RequestParam("password") String password) {
		
		model.addAttribute("password", password);
		
	}
		
	//�쉶�썝�깉�눜
	@RequestMapping(value="/userManage/memberInfoDelete",method=RequestMethod.GET)
	public String getDeleteMember(HttpSession session) throws Exception {
		
		String userid = (String)session.getAttribute("userid"); 
		
		String path = "c:\\Repository\\profile\\";
		
		//�쉶�썝 �봽濡쒗븘 �궗吏� �궘�젣
		MemberVO member = new MemberVO();
		member = service.memberInfoView(userid);		
		File file = new File(path + member.getStored_filename());
		file.delete();
		
		//�쉶�썝�씠 �뾽濡쒕뱶�븳 �뙆�씪 �궘�젣
		List<FileVO> fileList = boardService.fileInfoByUserid(userid);
		for(FileVO vo:fileList) {
			File f = new File(path + vo.getStored_filename());
			f.delete();
		}
		
		//寃뚯떆臾�,�뙎湲�,�뙆�씪�뾽濡쒕뱶 �젙蹂�, �쉶�썝�젙蹂� �쟾泥� �궘�젣
		service.memberInfoDelete((String)session.getAttribute("userid"));
		
		//紐⑤뱺 �꽭�뀡 �궘�젣
		session.invalidate();
		
		return "redirect:/";
	}
		
	//�슦�렪踰덊샇 寃��깋
	@RequestMapping(value="/member/addrSearch",method=RequestMethod.GET)
	public void getSearchAddr(@RequestParam("addrSearch") String addrSearch,
			@RequestParam("page") int pageNum,Model model) throws Exception {
		
		int postNum = 5;
		int startPoint = (pageNum -1)*postNum + 1; //�뀒�씠釉붿뿉�꽌 �씫�뼱 �삱 �뻾�쓽 �쐞移�
		int endPoint = pageNum*postNum;
		int listCount = 5;
		
		Page page = new Page();
		
		int totalCount = service.addrTotalCount(addrSearch);
		List<AddressVO> list = new ArrayList<>();
		list = service.addrSearch(startPoint, endPoint, addrSearch);

		model.addAttribute("list", list);
		model.addAttribute("pageListView", page.getPageAddress(pageNum, postNum, listCount, totalCount, addrSearch));
		
	}
	
}
