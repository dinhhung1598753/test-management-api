package com.demo.app.service;

import java.security.Principal;

public interface StudentTestService {

    void matchRandomTestForStudent(String classCode, Principal principal);
}
