package com.admindashboard.admindashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private int totalUsers;
    private int activeUsers;
    private int newUsersThisMonth;
    private int totalAdmins;
}
