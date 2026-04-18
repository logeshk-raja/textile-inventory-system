package com.example.demo.service;

import com.example.demo.entity.Admin;

public interface AdminService {

    Admin login(String username, String password);
}
