package com.central.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.central.UserCenterApp;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * SysUserControllerTest单元测试用例
 *
 * @author zlt
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserCenterApp.class})
public class SysUserControllerTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void whenQuerySuccess() throws Exception {
		String result = mockMvc.perform(
				get("/users/name/admin")
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println(result);
	}

	@Test
	public void whenGetInfoSuccess() throws Exception {
		String result = mockMvc.perform(get("/users/1")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("admin"))
				.andReturn().getResponse().getContentAsString();
		
		System.out.println(result);
	}

	@Test
	public void whenGetInfoNotExists() throws Exception {
		String result = mockMvc.perform(get("/users/9999")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertThat(result).isEmpty();
	}
	
	@Test
	public void whenGetUrl404() throws Exception {
		mockMvc.perform(get("/user/a")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	@Transactional
	public void whenDeleteSuccess() throws Exception {
		mockMvc.perform(delete("/users/1")
				.header("x-userid-header", 1)
				.header("x-user-header", "admin")
				.header("x-tenant-header", "webApp")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	public void whenSaveOrUpdateSuccess() throws Exception {
		String content = "{\"id\":1, \"username\":\"admin\", \"roleId\":\"1,2,3\"}";
		mockMvc.perform(post("/users/saveOrUpdate")
				.header("x-userid-header", 1)
				.header("x-user-header", "admin")
				.header("x-tenant-header", "webApp")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	public void whenUpdateSuccess() throws Exception {
		String content = "{\"id\":1, \"username\":\"admin\", \"roleId\":\"1,2,3\"}";
		mockMvc.perform(put("/users")
				.header("x-userid-header", 1)
				.header("x-user-header", "admin")
				.header("x-tenant-header", "webApp")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content))
				.andExpect(status().isOk());
	}
}
