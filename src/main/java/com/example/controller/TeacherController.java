package com.example.controller;

import com.example.dox.ProcessScore;
import com.example.service.TeacherService;
import com.example.service.UserService;
import com.example.vo.Code;
import com.example.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final UserService userService;
    @Value("${my.upload}")
    private String uploadDirectory;
    @GetMapping("/student")
    public Mono<ResultVo> getStudentsByTid(@RequestAttribute("id") String uid) {
        return teacherService.getTid(uid)
                .flatMap(teacherService::getStudentsByTid)
                .map(list -> ResultVo.success(Code.SUCCESS, Map.of("students",list)));
    }
    @GetMapping("/unselect")
    public Mono<ResultVo> getUnselectStudent() {
        return teacherService.getUnselectStudents()
                .map(s -> ResultVo.success(Code.SUCCESS,Map.of("students",s)));
    }
    @GetMapping("/file/{pid}/{number}")
    public Mono<ResultVo> getFileByPid(@PathVariable("pid") String pid, @PathVariable("number") String number) {
        return teacherService.getFileByPid(pid,number)
                .map(f -> ResultVo.success(Code.SUCCESS,Map.of("file",f)))
                .defaultIfEmpty(ResultVo.error(Code.ERROR,"该阶段未上传文件"));
    }
    @GetMapping("/download/{pid}/{number}")
    public Mono<Void> download(@PathVariable("pid") String pid, @PathVariable("number") String number,
                               ServerHttpResponse response) {
        return teacherService.getFileByPid(pid,number)
                .flatMap(f -> {
                    Path detail = Path.of(f.getDetail());
                    Path path = Path.of(uploadDirectory).resolve(detail);
                    // 将目标文件以二进制流的形式读入默认工厂缓冲区，并设置大小，
                    Flux<DataBuffer> read = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 1000 * 1024);
                    // 获取文件名
                    String name = URLEncoder.encode(path.getFileName().toString(), StandardCharsets.UTF_8);
                    HttpHeaders headers = response.getHeaders();
                    headers.set("filename", name); // 设置响应头文件名，用于前端截取
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // 设置响应的媒体类型为二进制流，表示这是一个文件下载。
                    /*
                    * 指示客户端将响应体作为附件进行处理，并指定附件的文件名；
                    * 否则客户端可能会在不弹出下载对话框的情况下直接在浏览器中打开文件，
                    * 或者将其保存到默认下载目录，而不询问用户下载的位置或文件名。
                    * */
                    headers.setContentDispositionFormData("attachment", name);
                    return response.writeWith(read);
                }).then();
    }
    @GetMapping("/group/{pid}")
    public Mono<ResultVo> getByGroup(@RequestAttribute("number") String number, @PathVariable("pid") String pid) {
        return teacherService.getGroup(number)
                .flatMap(group -> teacherService.getByGroup(group)
                        .flatMap(students -> teacherService.getByPid(pid)
                                .map(items -> ResultVo.success(Code.SUCCESS,Map.of("items",items,
                                        "students",students)))
                        )
                );
    }
    @GetMapping("name")
    public Mono<ResultVo> getTNameByNumber(@RequestAttribute("number") String number) {
        return teacherService.getTNameByNumber(number)
                .map(name -> ResultVo.success(Code.SUCCESS,Map.of("name",name)));
    }
    @PostMapping ("/processScore")
    public Mono<ResultVo> postProcessScore(@RequestBody ProcessScore processScore) {
        String sid = processScore.getStudentId();
        String pid = processScore.getProcessId();
        return teacherService.getPsBySidAndPid(sid,pid)
                .map(ps -> ResultVo.builder()
                        .code(Code.ERROR)
                        .message("该同学已评分，您添加的分数作废")
                        .data(Map.of("processScore",ps)).build())
                .switchIfEmpty(Mono.defer(() -> teacherService.postProcessScore(processScore)
                        .thenReturn(ResultVo.success(Code.SUCCESS))));
    }
    @GetMapping("/groups")
    public Mono<ResultVo> getAllGroup() {
        return teacherService.getAllGroup()
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("groups",list)));
    }
    @GetMapping("/teacher/{group}")
    public Mono<ResultVo> getTeachersByGroup(@PathVariable("group") Integer group) {
        return teacherService.getTeachersByGroup(group)
                .map(ts -> ResultVo.success(Code.SUCCESS,Map.of("teachers",ts)));
    }
}
