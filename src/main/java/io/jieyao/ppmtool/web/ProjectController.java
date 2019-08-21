package io.jieyao.ppmtool.web;

import io.jieyao.ppmtool.domain.Project;
import io.jieyao.ppmtool.services.MapValidationService;
import io.jieyao.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

// do not put too much logic in the controller
@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MapValidationService mapValidationService;

    @PostMapping("")
    // create or update
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal) {
        // BindingResult is used together with @Valid to see validation results
        // but it only checks basic criteria, not on the database constraint level
        if (result.hasErrors()) {
            return mapValidationService.getErrors(result);
        }
                                                            // setup relationship between user and project
        Project project1 = projectService.saveOrUpdateProject(project, principal.getName());
        return new ResponseEntity<Project>(project1, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId, Principal principal) {
        Project project = projectService.findProjectByIdentifier(projectId, principal.getName());
        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    @GetMapping("")
    //pass in the principal so that only get projects for the current user
    public ResponseEntity<Iterable<Project>> getAllProjects(Principal principal) {
        Iterable<Project> projects = projectService.findAllProjects(principal.getName());
        return new ResponseEntity<Iterable<Project>>(projects, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId, Principal principal) {
        String result = projectService.deleteProjectByIdentifier(projectId, principal.getName());
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
