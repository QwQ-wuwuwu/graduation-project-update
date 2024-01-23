package com.example.controller;

import com.example.dox.File;
import com.example.dox.Teacher;
import com.example.pojo.StartAndEndTime;
import com.example.service.StudentService;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/student")
@RestController
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final StartAndEndTime time;
    private final UserService userService;
    @Value("${my.upload}")
    private String uploadDirectory;
    @GetMapping("/info")
    public Mono<ResultVo> getInfo() {
        return Mono.just(ResultVo.success(Code.SUCCESS));
    }
    @GetMapping("/teacher")
    public Mono<ResultVo> getAllTeachers(@RequestAttribute("number") String number) {
        Mono<List<Teacher>> allTeacher = studentService.getAllTeacher();
        return Mono.just(LocalDateTime.now())
                .flatMap(now -> {
                    if (time.getStart() == null) {
                        return Mono.just(ResultVo.error(Code.ERROR,"未设置开放时间"));
                    }
                    if (now.isBefore(time.getStart()) || now.isAfter(time.getEnd())) {
                        return Mono.just(ResultVo.builder()
                                .code(Code.NOT_START)
                                .data(Map.of("time",time)).message("不在系统开放时间内").build());
                    }
                    return studentService.getStudentByNumber(number)
                            .flatMap(s -> allTeacher.map(ts ->
                                    ResultVo.success(Code.SUCCESS, Map.of("teachers",ts))))
                            .defaultIfEmpty(ResultVo.error(Code.ERROR,"已选导师,不可再选"));
                });
    }
    @GetMapping("/group")
    public Mono<ResultVo> countOfGroup() {
        return studentService.countOfGroup()
                .map(count -> ResultVo.success(Code.SUCCESS, Map.of("group",count)));
    }
    @PutMapping("/tutors/{tid}/{group}")
    public Mono<ResultVo> putSelection(@PathVariable("tid") String tid, @PathVariable("group") Integer groupId,
                                       @RequestAttribute("number") String number) {
        return studentService.selectTeacher(tid,number,groupId)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
    @GetMapping("/process")
    public Mono<ResultVo> getAllProcess() {
        return userService.listProcess()
                .map(ps -> ResultVo.success(Code.SUCCESS,Map.of("processes",ps)));
    }
    @PostMapping("/upload/{pid}/{pname}")
    public Mono<ResultVo> postFile(@PathVariable("pid") String pid, @PathVariable("pname") String pname,
                                   @RequestAttribute("number") String number,
                                   @RequestPart("file") Mono<FilePart> file) {
        return studentService.findByNumberAndPid(number, pid)
                .flatMap(f -> file.flatMap(filePart -> {
                    Path p1 = Path.of(uploadDirectory).resolve(Path.of(f.getDetail()));
                    String detail = Path.of(pname).resolve(filePart.filename()).toString();
                    Path p2 = Path.of(uploadDirectory).resolve(pname);
                    return Mono.deferContextual(ctx -> Mono.fromCallable(() -> Files.deleteIfExists(p1)) //deferContextual返回一个可以访问上下文的Mono
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.defer(() -> filePart.transferTo(p2.resolve(filePart.filename()))))
                            .then(Mono.defer(() -> studentService.updateFile(number, pid, detail)))
                            .thenReturn(ResultVo.success(Code.SUCCESS)));
                }))
                .switchIfEmpty(Mono.defer(() -> file.flatMap(filePart -> {
                    File pf = File.builder()
                            .processId(pid)
                            .studentNumber(number)
                            .detail(Path.of(pname).resolve(filePart.filename()).toString())
                            .build();
                    Path p = Path.of(uploadDirectory).resolve(pname);
                    return Mono.fromCallable(() -> Files.createDirectories(p))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(path -> {
                                Path finalFile = path.resolve(filePart.filename());
                                return filePart.transferTo(finalFile);
                            })
                            .then(Mono.defer(() -> studentService.postFile(pf)))
                            .thenReturn(ResultVo.success(Code.SUCCESS));
                })))
                .onErrorResume(err -> Mono.just(ResultVo.error(Code.ERROR, "文件保存失败")));
    }
    @GetMapping("/score/{pid}")
    public Mono<ResultVo> getProcess_score(@RequestAttribute("number") String number, @PathVariable("pid") String pid) {
        System.out.println(number);
        return studentService.getSidByNumber(number)
                .flatMap(sid -> studentService.getProcessScore(sid,pid)
                        .map(ps -> ResultVo.success(Code.SUCCESS,Map.of("processScore",ps)))
                        .defaultIfEmpty(ResultVo.error(Code.ERROR,"该阶段未进行评分")));
    }
    @GetMapping("/process/{pid}")
    public Mono<ResultVo> getProcessById(@PathVariable("pid") String pid) {
        return studentService.getProcessById(pid)
                .map(items -> ResultVo.success(Code.SUCCESS,Map.of("items",items)));
    }
    @GetMapping("/score")
    public Mono<ResultVo> getPSBySid(@RequestAttribute("number") String number) {
        return studentService.getPsBySid(number)
                .map(ps -> ResultVo.success(Code.SUCCESS,Map.of("processScore",ps)));
    }
}
