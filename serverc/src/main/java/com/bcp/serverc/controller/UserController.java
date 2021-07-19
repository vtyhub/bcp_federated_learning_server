package com.bcp.serverc.controller;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.PP;
import com.bcp.serverc.crypto.BCP;
import com.bcp.general.crypto.BcpKeyPair;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.impl.UserServiceImpl;

/**
 * 处理注册，登录，用户信息等接口
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserServiceImpl userSrv;

	// @Autowired
	// private AuthenticationManager authenticationManager;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Object register(@RequestBody @Valid User user) {
		Object ret = userSrv.register(user);
		return ret;
	}

	/**
	 * 查询用户列表
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object getUserList() {
		Object ret = userSrv.getUserList();
		return ret;
	}

	/**
	 * 获取当前用户昵称
	 * 
	 * @return
	 */
	@RequestMapping(value = "/nickname", method = RequestMethod.GET)
	public Object getCurrentLoginUserNickname() {
		User ret = UserServiceImpl.getCurrentLoginUser();
		if (ret != null) {
			return ret.getNickname();
		}
		return ret;
	}

	// @RequestMapping(value = "/login", method = RequestMethod.POST)
	// public Object login(@RequestBody User user, HttpServletRequest request)
	// throws IOException {
	// String username = user.getUsername();
	// String password = user.getPassword();
	//
	// // 系统登录认证
	//
	// return null;
	// }

	// 临时客户端用生成公钥及加解密接口
	@RequestMapping(value = "/keyGen", method = RequestMethod.POST)
	public BcpKeyPair keyGen(@RequestBody PP pp) {
		BcpKeyPair keyGen = BCP.keyGen(pp.getN(), pp.getG());
		return keyGen;
	}

	@RequestMapping(value = "/enc", method = RequestMethod.POST)
	public List<BcpCiphertext> enc(BigInteger N, BigInteger g, BigInteger h,
			@Valid @RequestBody List<BigInteger> mList) {
		List<BcpCiphertext> rtn = mList.stream().map((m) -> BCP.enc(N, g, h, m)).collect(Collectors.toList());
		return rtn;
	}

	@RequestMapping(value = "/dec", method = RequestMethod.POST)
	public List<String> dec(BigInteger N, BigInteger a, @Valid @RequestBody List<BcpCiphertext> cList) {
		List<String> rtn = cList.stream().map((c) -> BCP.dec(N, a, c).toString()).collect(Collectors.toList());
		return rtn;
	}

}
