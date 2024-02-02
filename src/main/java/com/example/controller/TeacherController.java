package com.example.controller;

import com.example.dox.ProcessScore;
import com.example.service.TeacherService;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherService teacherService;
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
    @GetMapping("/download/{pid}/{number}/{PNumber}")
    public Mono<Void> download(@PathVariable("pid") String pid, @PathVariable("number") String number,
                               @PathVariable("PNumber") Integer PNumber, ServerHttpResponse response) {
        return teacherService.getFileByPid(pid,number,PNumber)
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
                })
                .then();
    }
    @GetMapping("/group/{auth}")
    public Mono<ResultVo> getByGroup(@RequestAttribute("number") String number, @PathVariable("auth") String auth) {
        if (auth.equals("audit")) {
            return teacherService.getGroup(number)
                    .flatMap(group -> teacherService.getByGroup(group)
                            .map(students -> ResultVo.success(Code.SUCCESS,Map.of("students",students)))
                    );
        }
        return teacherService.getStudentsByAuth(number)
                .map(students -> ResultVo.success(Code.SUCCESS, Map.of("students",students)));
    }
    @GetMapping("/student/group")
    public Mono<ResultVo> getStudentsByGroup(@RequestAttribute("number") String number) {
        return teacherService.getGroup(number)
                .flatMap(group -> teacherService.getByGroup(group)
                        .map(students -> ResultVo.success(Code.SUCCESS,Map.of("students",students)))
                );
    }
    @GetMapping("/processScore/{pid}/{sid}/{tid}")
    public Mono<ResultVo> scoreOrGetInfo(@PathVariable("pid") String pid, @PathVariable("sid") String sid,
                                         @PathVariable("tid") String tid) {
        return teacherService.getProcessScore(sid,pid,tid)
                .flatMap(ps -> teacherService.getProcessScores(sid, pid)
                        .map(pSs -> ResultVo.success(Code.SUCCESS, Map.of("flag",1,
                                "processScores",pSs)))
                )
                .switchIfEmpty(Mono.defer(() -> teacherService.getStudentById(sid)
                        .map(s -> ResultVo.success(Code.SUCCESS,Map.of("student",s)))
                        )
                );
    }
    @GetMapping("/processScore/{pid}/{tid}")
    public Mono<ResultVo> getProcessScoreByPidAndTid(@PathVariable("pid") String pid, @PathVariable("tid") String tid) {
        return teacherService.getProcessScoresByPidAndTid(tid,pid)
                .map(list -> ResultVo.success(Code.SUCCESS, Map.of("processScores",list)));
    }
    @GetMapping("/file/{pid}/{number}/{PNumber}")
    public Mono<ResultVo> getFile(@PathVariable("pid")String pid, @PathVariable("number") String number,
                                  @PathVariable("PNumber") Integer PNumber) {
        return teacherService.getFile(pid,number,PNumber)
                .map(file -> ResultVo.success(Code.SUCCESS,Map.of("file",file)));
    }
    @PostMapping ("/processScore")
    public Mono<ResultVo> postProcessScore(@RequestBody ProcessScore processScore) {
        return teacherService.postProcessScore(processScore)
                .thenReturn(ResultVo.success(Code.SUCCESS));
    }
    @DeleteMapping("/processScore/{pid}/{sid}/{tid}")
    public Mono<ResultVo> deleteProcessScore(@PathVariable("pid") String pid, @PathVariable("sid") String sid,
                                             @PathVariable("tid") String tid) {
        return teacherService.deleteProcessScore(pid,sid,tid)
                .map(r -> ResultVo.success(Code.SUCCESS))
                .onErrorReturn(ResultVo.error(Code.ERROR,"删除失败"));
    }
    @GetMapping("/teacher")
    public Mono<ResultVo> getTeachersByGroup(@RequestAttribute("number") String number) {
        return teacherService.getGroup(number)
                .flatMap(group -> teacherService.getTeachersByGroup(group)
                        .map(ts -> ResultVo.success(Code.SUCCESS,Map.of("teachers",ts)))
                );
    }
    @GetMapping("/processScores")
    public Mono<ResultVo> getAllProcessScores() {
        return teacherService.getAllProcessScores()
                .map(list -> ResultVo.success(Code.SUCCESS,Map.of("processScores",list)));
    }
}
