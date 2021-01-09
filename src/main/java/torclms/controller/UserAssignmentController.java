package torclms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import torclms.dto.StageAttemptDto;
import torclms.dto.StageAttemptInfoDto;
import torclms.dto.UserCourseAssignment;
import torclms.entity.AssignmentStatus;
import torclms.exception.ResourceNotFoundException;
import torclms.model.Course;
import torclms.model.StageAttempt;
import torclms.model.User;
import torclms.model.UserAssignment;
import torclms.service.CourseService;
import torclms.service.UserAssignmentService;
import torclms.service.UserAssignmentServiceImpl;
import torclms.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserAssignmentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserAssignmentService userAssignmentService;

    @GetMapping("/assignments")
    public List<UserAssignment> getAllAssignments () {
        return userAssignmentService.getAllAssignments();
    }

    @GetMapping("/assignments/{assignmentId}")
    public UserAssignment getAssignment (@PathVariable(value = "assignmentId") Long assignmentId) {
        return userAssignmentService.getAssignmentById(assignmentId).orElseThrow(() -> new ResourceNotFoundException("UserAssignment", "id", assignmentId));
    }

    @PostMapping("/assignments/active-user")
    public User assignStage (@RequestBody UserCourseAssignment assignment) {
        Course course = courseService.findCourseById(assignment.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("Course", "id", assignment.getCourseId()));
        User user = userService.findById(assignment.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", assignment.getUserId()));

        return userService.assignCourse(user, course);
    }

    @GetMapping("/assignments/active-user")
    public List<UserAssignment> getCoursesAssignedToActiveUser (@AuthenticationPrincipal final Principal authUser) {
        User user = userService.findUserByEmail(authUser.getName());
        List<UserAssignment> userAssignments = userService.findAssignmentsByUserId(user.getUserId());

        return userAssignments;
    };

    @GetMapping("/assignments/attempt-stage")
    public List<StageAttemptInfoDto> getAllAttempts () {
        return userAssignmentService.getAllStageAttempts();
    }

    @PostMapping("/assignments/attempt-stage")
    public UserAssignment postAttemptStage (@RequestBody StageAttemptDto stageAttemptDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        return userAssignmentService.attemptStage(user, stageAttemptDto);
    }

    // TODO: Only admin and manager should be able to access this route
    @GetMapping("/assignments/status/{assignmentStatus}")
    public List<UserAssignment> getUserAssignmentsByStatus (@PathVariable(value = "assignmentStatus") AssignmentStatus assignmentStatus) {
        List<UserAssignment> assignments = userAssignmentService.getAssignmentsByStatus(assignmentStatus);

        assignments.forEach(assignment -> assignment.getAssignedUser());

        return assignments;
    }

    // TODO: Only manager should be able to access this route
    @PostMapping("assignments/unlock/{assignmentId}")
    public UserAssignment unlockAssignment (@PathVariable(value = "assignmentId") Long assignmentId) {
        return userAssignmentService.unlockAssignment(assignmentId);
    }

}