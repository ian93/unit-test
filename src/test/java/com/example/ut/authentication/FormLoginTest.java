package com.example.ut.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ut.controller.LoginController;
import com.example.ut.SecurityTestConfig;
import com.example.ut.service.LoginService;

@Import(value = SecurityTestConfig.class) // 載入測試用的 UserDetailsService
@WebMvcTest(value = LoginController.class) // 使用 @WebMvcTest 建立 Servlet 測試環境, 並掃描 @Controller / @ControllerAdvice
class FormLoginTest {

    // @WebMvcTest 提供 MockMvc, 不須自行 setup
    @Autowired
    private MockMvc mvc;

    // @WebMvcTest 只掃描 @Controller / @ControllerAdvice, 其依賴需要自行 Mock
    @MockBean
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        assertNotNull(mvc);
        when(loginService.login("test1", "test1")).thenReturn(true);
        when(loginService.login("test2", "test2")).thenReturn(true);
        when(loginService.login("test3", "test3")).thenReturn(true); // 設定不屬於 UserDetailsService 的帳號
        when(loginService.login("manager", "manager")).thenReturn(true);
        when(loginService.login("admin", "admin")).thenReturn(true);
    }

    @Test
    void givenCorrectUser_whenFormLogin_shouldRedirectToIndex() throws Exception {
        mvc.perform(formLogin("/login").user("test1").password("test1"))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andDo(print());
    }

    @Test
    void givenIncorrectUser_whenFormLogin_shouldRedirectToLoginWithQueryStringError() throws Exception {
        mvc.perform(formLogin("/login").user("test3").password("test3"))
                .andExpect(unauthenticated()) // 不通過 SecurityTestConfig.class 的 UserDetailsService
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }

    @Test
    void givenUserCredentialsInGeneralSettings_whenFormLogin_shouldRedirectToLoginWithQueryStringError() throws Exception {
        mvc.perform(formLogin("/login").user("user1").password("user1"))
                .andExpect(unauthenticated()) // 不通過 SecurityTestConfig.class 的 UserDetailsService
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }
}
