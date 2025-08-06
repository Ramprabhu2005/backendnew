package taskmanagementsystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import taskmanagementsystem.dto.ApiResponse;
import taskmanagementsystem.exception.ResourceNotFoundException;
import taskmanagementsystem.model.Task;
import taskmanagementsystem.model.User;
import taskmanagementsystem.repository.TaskRepository;
import taskmanagementsystem.repository.UserRepository;
import taskmanagementsystem.service.TaskHistoryService;
import taskmanagementsystem.service.TaskService;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskHistoryService taskHistoryService;
    private final ObjectMapper objectMapper;

    public TaskServiceImpl(TaskRepository taskRepository, 
                         UserRepository userRepository,
                         TaskHistoryService taskHistoryService,
                         ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskHistoryService = taskHistoryService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApiResponse createTask(Task task, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found, Id: " + userId));
        task.setUser(user);
        task.setCompleted(false); // Ensure new tasks are not completed by default

        Task savedTask = taskRepository.save(task);
        
        // Log the task creation
        taskHistoryService.logTaskCreation(savedTask, user);
        
        return new ApiResponse("Task Saved", savedTask);
    }

    @Override
    public ApiResponse getTaskById(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(()-> new ResourceNotFoundException("Task not found, Id: " + taskId));
        return new ApiResponse("Found task", task);
    }

    @Override
    public List<Task> getAllTasks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found, Id" + userId));
        List<Task> taskList = taskRepository.findAllByUserId(user.getId());
        return taskList;
    }

    private String taskToJson(Task task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize task\"}";
        }
    }

    @Override
    public ApiResponse updateTask(Task task, Integer id) {
        Task foundTask = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found, Id: " + id));
        
        // Save the previous state for history
        String previousState = taskToJson(foundTask);
        
        // Update the task
        foundTask.setTask(task.getTask());
        foundTask.setDetails(task.getDetails());
        foundTask.setCompleted(task.getCompleted());
        
        Task updatedTask = taskRepository.save(foundTask);
        
        // Log the update
        taskHistoryService.logTaskUpdate(updatedTask, foundTask.getUser(), previousState);
        
        return new ApiResponse("Task updated successfully", updatedTask);
    }

    @Override
    public void deleteTask(Integer id) {
        System.out.println("[DEBUG] Entering deleteTask with id: " + id);
        try {
            Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found, Id: " + id));
            
            // Save the task state before deletion
            String previousState = taskToJson(task);
            User user = task.getUser();
            
            // Delete the task
            taskRepository.delete(task);
            
            // Log the deletion
            taskHistoryService.logTaskDeletion(task, user, previousState);
            System.out.println("[DEBUG] Successfully deleted task with id: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting task with id " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete task with id " + id, e);
        }
    }

    @Override
    public ApiResponse doneTask(Integer id) {
        return markTaskAsCompleted(id);
    }

    @Override
    public ApiResponse markTaskAsCompleted(Integer id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found, Id: " + id));
            
        if (!task.getCompleted()) {
            task.setCompleted(true);
            Task updatedTask = taskRepository.save(task);
            
            // Log the completion
            taskHistoryService.logTaskCompletion(updatedTask, updatedTask.getUser());
            
            return new ApiResponse("Task marked as completed", updatedTask);
        }
        
        return new ApiResponse("Task was already completed", task);
    }

    @Override
    public ApiResponse markTaskAsPending(Integer id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found, Id: " + id));
            
        if (task.getCompleted()) {
            task.setCompleted(false);
            Task updatedTask = taskRepository.save(task);
            
            // Log the status change to pending
            taskHistoryService.logTaskUpdate(updatedTask, updatedTask.getUser(), 
                "Task status changed to PENDING");
            
            return new ApiResponse("Task marked as pending", updatedTask);
        }
        
        return new ApiResponse("Task was already pending", task);
    }

    @Override
    public ApiResponse pendingTask(Integer id) {
        return markTaskAsPending(id);
    }
}
