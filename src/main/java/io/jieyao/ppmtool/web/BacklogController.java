package io.jieyao.ppmtool.web;

import io.jieyao.ppmtool.domain.Project;
import io.jieyao.ppmtool.domain.ProjectTask;
import io.jieyao.ppmtool.services.MapValidationService;
import io.jieyao.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationService mapValidationService;

    @PostMapping("/{projectId}")
    public ResponseEntity<?> addTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                              @PathVariable String projectId, Principal principal) {
        if (result.hasErrors()) {
            return mapValidationService.getErrors(result);
        }
        ProjectTask projectTask1 = projectTaskService.addProjectTask(projectId, projectTask, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public Iterable<ProjectTask> getProjectTasks(@PathVariable String projectId, Principal principal) {
        return projectTaskService.findTasksByProjectId(projectId, principal.getName());
    }

    @GetMapping("/{projectId}/{taskId}")
    public ResponseEntity<?> getProjectTask(@PathVariable String projectId, @PathVariable String taskId, Principal principal) {
        ProjectTask projectTask = projectTaskService.findTaskByProjectSequence(projectId, taskId, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PutMapping("/{projectId}/{taskId}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask updatedTask, BindingResult result,
                                               @PathVariable String projectId, @PathVariable String taskId, Principal principal) {
        if (result.hasErrors()) {
            return mapValidationService.getErrors(result);
        }
        ProjectTask updatedTask1 = projectTaskService.updateByProjectSequence(updatedTask, projectId, taskId, principal.getName());
        return new ResponseEntity<ProjectTask>(updatedTask1, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/{taskId}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String projectId, @PathVariable String taskId, Principal principal) {
        projectTaskService.deleteTaskBySequence(projectId, taskId, principal.getName());
        return new ResponseEntity<String>("Project Task " + taskId + " was deleted succesfully.", HttpStatus.OK);
    }

}
