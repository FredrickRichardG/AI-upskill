package com.school.attendance.dto;

import com.school.attendance.constant.AttendanceConstants;
import com.school.attendance.model.AttendanceStatus;
import com.school.attendance.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    @NotNull(
        message = AttendanceConstants.MSG_STUDENT_ID_REQUIRED,
        groups = {ValidationGroups.Create.class, ValidationGroups.Bulk.class}
    )
    private UUID studentId;

    @NotNull(
        message = AttendanceConstants.MSG_DATE_REQUIRED,
        groups = {ValidationGroups.Create.class, ValidationGroups.Bulk.class}
    )
    private LocalDate date;

    @NotNull(
        message = AttendanceConstants.MSG_STATUS_REQUIRED,
        groups = {ValidationGroups.Create.class, ValidationGroups.Bulk.class}
    )
    private AttendanceStatus status;

    @NotNull(
        message = AttendanceConstants.MSG_MARKED_BY_REQUIRED,
        groups = {ValidationGroups.Create.class, ValidationGroups.Bulk.class}
    )
    private UUID markedById;
} 