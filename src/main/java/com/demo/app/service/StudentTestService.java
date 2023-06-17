package com.demo.app.service;

import java.io.IOException;
import java.security.Principal;

public interface StudentTestService {

    void matchRandomTestForStudent(String classCode, Principal principal);

    void markingOfflineAnswer() throws IOException;
}
