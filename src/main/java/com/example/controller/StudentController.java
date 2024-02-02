package com.example.controller;

import com.example.dox.File;
import com.example.dox.Teacher;
import com.example.exception.XException;
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
                            .switchIfEmpty(Mono.defer(() -> studentService.getStudent(number)
                                    .map(s -> ResultVo.builder().code(Code.ERROR)
                                            .message("已选导师不可再选！")
                                            .data(Map.of("student",s)).build())));
                });
    }
    @GetMapping("/group")
    public Mono<ResultVo> countOfGroup() {
        return studentService.countOfGroup()
                .map(count -> ResultVo.success(Code.SUCCESS, Map.of("group",count)));
    }
    @PutMapping("/tutors/{tid}/{tname}")
    public Mono<ResultVo> putSelection(@PathVariable("tid") String tid, @PathVariable("tname") String tname,
                                       @RequestAttribute("number") String number) {
        return studentService.selectTeacher(tid, number, tname)
                .thenReturn(ResultVo.success(Code.SUCCESS))
                .onErrorResume(XException.class, x -> Mono.just(ResultVo.builder()
                        .code(x.getCode())
                        .message(x.getMessage())
                        .build()));
    }
    @GetMapping("/process")
    public Mono<ResultVo> getAllProcess() {
        return studentService.getAttachProcess()
                .map(ps -> ResultVo.success(Code.SUCCESS,Map.of("processes",ps)));
    }
    @PostMapping("/upload/{pid}/{pname}/{numberS}")
    public Mono<ResultVo> postFile(@PathVariable("pid") String pid, @PathVariable("pname") String pname,
                                   @RequestAttribute("number") String number, @PathVariable("numberS") Integer numberS,
                                   @RequestPart("file") Mono<FilePart> file) {
        return studentService.findByNumberAndPid(number, pid, numberS)
                .flatMap(f -> file.flatMap(filePart -> {
                    Path p1 = Path.of(uploadDirectory).resolve(Path.of(f.getDetail()));
                    String detail = Path.of(pname).resolve(filePart.filename()).toString();
                    Path p2 = Path.of(uploadDirectory).resolve(pname);
                    return Mono.deferContextual(ctx -> Mono.fromCallable(() -> Files.deleteIfExists(p1)) //deferContextual返回一个可以访问上下文的Mono
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.defer(() -> filePart.transferTo(p2.resolve(filePart.filename()))))
                            .then(Mono.defer(() -> studentService.updateFile(number, pid, detail, numberS)))
                            .thenReturn(ResultVo.success(Code.SUCCESS)));
                }))
                .switchIfEmpty(Mono.defer(() -> file.flatMap(filePart -> {
                    File pf = File.builder()
                            .processId(pid)
                            .studentNumber(number)
                            .number(numberS)
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
    @GetMapping("/files")
    public Mono<ResultVo> getFilesByStu(@RequestAttribute("number") String number) {
        return studentService.getFilesByStu(number)
                .map(files -> ResultVo.success(Code.SUCCESS, Map.of("files",files)));
    }
}
