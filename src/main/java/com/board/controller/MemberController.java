package com.board.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.board.domain.UserVO;
import com.board.service.UserService;

@Controller
@RequestMapping("/member/*")
public class MemberController {
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	private final UserService userService;
	
	@Inject
	public MemberController(UserService userService) {
		this.userService = userService;
	}
	
	// 회원가입 페이지
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerGET() throws Exception {
		return "/member/register";
	}

	//아이디 중복체크
	//json을 View 단으로 전송하기위해 @ResponseBody 사용
	@ResponseBody
	@RequestMapping(value="/idChk", method = RequestMethod.POST)
	public int idChk(UserVO userVO) throws Exception {
		int result = userService.idChk(userVO.getUserId());
		return result;
	}

	// 회원가입 처리
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerPOST(UserVO userVO, RedirectAttributes rttr) throws Exception {
		int result = userService.idChk(userVO.getUserId());
		try {
			if(result == 1) {
				return "redirect:/member/register";
			}else if (result == 0){
				//비밀번호 암호화 처리 
				//BCrypt.hashpw() 메서드는 첫번째 파라미터에는 암호화할 비밀번호
				//두번째 파라미터는 BCrypt.gensalt() 받고 암호화된 비밀번호를 리턴한다
				String hashedPW = BCrypt.hashpw(userVO.getPw(), BCrypt.gensalt());
				
				//암호화된 비밀번호를 다시 회원 객체에 저장
				userVO.setPw(hashedPW);
				
				//서비스 회원가입 메서드 호출
				userService.register(userVO);
			}
		}catch (Exception e) {
			throw new RuntimeException();
		}
		
		//String을 전달
		//post 형식으로 전달, 한 번만 사용되면 사라진다
		rttr.addFlashAttribute("msg", "REGISTERED");
		
		//회원 가입 완료 후 로그인 페이지로 이동
		return "redirect:/member/login";
	}
	
	//회원 수정 페이지 이동
	@RequestMapping(value = "/userModifyView", method = RequestMethod.GET)
	public String userModifyGET() throws Exception {
		return "/member/userModifyView";
	}
	
	//회원 수정처리
	@RequestMapping(value = "/userModify", method = RequestMethod.POST) 
	public String userModifyPOST(UserVO userVO, HttpSession httpsession) throws Exception { 
		//비밀번호 암호화 처리 
		String hashedPW = BCrypt.hashpw(userVO.getPw(), BCrypt.gensalt());
		userVO.setPw(hashedPW);

		//서비스 회원정보 수정 메서드 호출
		userService.userUpdate(userVO);
		
		//정보를 수정했으니 다시 로그인 하기위해 세션값을 날린다
		httpsession.invalidate();
		return "redirect:/"; 
	}
	
	//회원 블락 페이지 이동
	@RequestMapping(value = "/userBlockView", method = RequestMethod.GET)
	public String userBlockGET() throws Exception {
		return "/member/userBlockView";
	}
	
	//회원 블락 처리
	@RequestMapping(value = "/userBlock", method = RequestMethod.POST) 
	public String userBlockPOST(UserVO userVO, HttpSession httpsession, RedirectAttributes rttr) throws Exception { 
		//로그인 세션정보를 가져오기
		UserVO login = (UserVO) httpsession.getAttribute("login");
		
		// 세션에있는 비밀번호, 아이디 가져오기
		String sessionId = login.getUserId();
		String sessionPw = login.getPw();
		
		// input으로 들어오는 비밀번호
		String voPw = userVO.getPw();
		
		//비밀번호 확인 불일치시 처음으로 돌아가기
		if (login == null || !BCrypt.checkpw(voPw, sessionPw)) {
			logger.info("비밀번호 불일치!!");
			rttr.addFlashAttribute("msg", false);
			return "redirect:/member/userBlockView";
		}

		userService.userBlock(userVO);
		
		//정보를 수정했으니 다시 로그인 하기위해 세션값을 날린다
		httpsession.invalidate();
		return "redirect:/"; 
	}
	
	//아이디 찾기 페이지 이동
	@RequestMapping(value = "/findIdView", method = RequestMethod.GET)
	public String findIdGET() throws Exception {
		return "/member/findIdView";
	}
	
	//아이디 찾기
	@RequestMapping(value = "/findId", method = RequestMethod.POST) 
	public String findIdPOST(UserVO userVO, RedirectAttributes rttr, HttpServletRequest request, Model model) throws Exception { 
		logger.info("아이디 찾기");
		
		// input으로 들어오는 이메일 
		// 이메일 등록여부 확인
		String findId = userService.findId(userVO.getEmail());
		
		//이메일을 찾을 수 없는 경우
		if (findId == null) {
			rttr.addFlashAttribute("msg", false);
			return "redirect:/member/findIdView";
		}
		
		//세션을 저장소 생성 
		HttpSession httpSession = request.getSession();
		//찾은 아이디를 findId변수로 세션저장
		httpSession.setAttribute("findId", findId);
		
		return "redirect:/member/findIdOk"; 
	}
	
	//찾은 아이디 확인
	@RequestMapping(value = "/findIdOk", method = RequestMethod.GET)
	public String findIdOkGET() throws Exception {
		return "/member/findIdOk";
	}

}



