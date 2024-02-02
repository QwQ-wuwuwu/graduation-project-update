package com.example.controller;

import com.example.dox.Process;
import com.example.dox.Student;
import com.example.dox.Teacher;
import com.example.dox.User;
import com.example.service.AdminService;
import com.example.service.TeacherService;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final TeacherService teacherService;
    @PutMapping("/time")
    public Mono<ResultVo> setTime(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end) {
        return adminService.setTime(start,end)
                .map(time -> ResultVo.success(Code.SUCCESS,Map.of("time",time)));
    }
    @PutMapping("/password/{number}/{newPw}")
    public Mono<ResultVo> updatePassword(@PathVariable("number")String number,
                                         @PathVariable("newPw")String newPw) {
        return adminService.updatePassword(number,newPw)
                .thenReturn(ResultVo.builder().code(Code.SUCCESS).build())
                .onErrorResume(err -> Mono.just(ResultVo.error(Code.ERROR,err.getMessage())));
    }
    @PutMapping("/password/{number}")
    public Mono<ResultVo> updatePasswordByUser(@PathVariable("number")String number) {
        return adminService.updatePasswordByUser(number)
                .thenReturn(ResultVo.success(Code.SUCCESS))
                .onErrorResume(err -> Mono.just(ResultVo.error(Code.ERROR,err.getMessage())));
    }
    @PutMapping("/password")
    public Mono<ResultVo> updateAllPassword() {
        return adminService.updateAllPassword()
                .thenReturn(ResultVo.success(Code.SUCCESS))
                .onErrorResume(err -> Mono.just(ResultVo.error(Code.ERROR,err.getMessage())));
    }
    @PostMapping("/process")
    public Mono<ResultVo> postProcess(@RequestBody Process process) {
        System.out.println(process);
        return adminService.postProcess(process)
                .flatMap(r -> userService.listProcess())
                .map(ps -> ResultVo.success(Code.SUCCESS, Map.of("processes",ps)));
    }
    @DeleteMapping("/process/{pid}")
    public Mono<ResultVo> deleteProcess(@PathVariable("pid") String pid) {
        return adminService.deleteProcess(pid)
                .thenReturn(ResultVo.success(Code.SUCCESS))
                .onErrorResume(err -> Mono.just(ResultVo.error(Code.ERROR,"删除失败" + err.getMessage())));
    }
    @GetMapping("/process")
    public Mono<ResultVo> listProcess() {
        return userService.listProcess()
                .map(ps -> ResultVo.success(Code.SUCCESS, Map.of("processes",ps)))
                .defaultIfEmpty(ResultVo.success(Code.SUCCESS,Map.of()));
    }
    @PostMapping("/student")
    public Mono<ResultVo> postStudents(@RequestBody List<Student> students) {
        return adminService.postStudents(students, User.ROLE_STUDENT)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
    @PostMapping("/teacher")
    public Mono<ResultVo> postTeachers(@RequestBody List<Teacher> teachers) {
        return adminService.postTeachers(teachers,User.ROLE_TEACHER)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
    @GetMapping("/confirm/{pid}/{pname}")
    public Mono<ResultVo> getPidAndName(@PathVariable("pid") String pid, @PathVariable("pname") String pname) {
        return Mono.just(ResultVo.success(Code.SUCCESS,Map.of("processId",pid,
                "processName",pname)));
    }
    @GetMapping("/groups")
    public Mono<ResultVo> getGroup() {
        return teacherService.getAllGroup()
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("groups",list)));
    }
    @GetMapping("/student/{gid}")
    public Mono<ResultVo> getStudentsByGroup(@PathVariable("gid") Integer group) {
        return adminService.getStudentsByGroup(group)
                .map(list -> ResultVo.success(Code.SUCCESS, Map.of("students",list)));
    }
    @GetMapping("/students")
    public Mono<ResultVo> getALlStudents() {
        return adminService.getAllStudents()
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("students",list)));
    }
    @PutMapping("/group/{sid}/{newGroup}")
    public Mono<ResultVo> updateGroup(@PathVariable("sid") String sid, @PathVariable("newGroup") Integer newGroup) {
        return adminService.updateGroup(sid,newGroup)
                .map(i -> ResultVo.success(Code.SUCCESS))
                .defaultIfEmpty(ResultVo.error(Code.ERROR,"更新失败"));
    }
    @PutMapping ("/group")
    public Mono<ResultVo> postStudentsGroup(@RequestBody List<Student> students) {
        return adminService.postStudentsGroup(students)
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("students",list)))
                .defaultIfEmpty(ResultVo.error(Code.ERROR,"更新失败"));
    }
    @PutMapping ("/projectTitle")
    public Mono<ResultVo> postStudentsProjectTitle(@RequestBody List<Student> students) {
        return adminService.postStudentsProjectTitle(students)
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("students",list)))
                .defaultIfEmpty(ResultVo.error(Code.ERROR,"更新失败"));
    }
}
