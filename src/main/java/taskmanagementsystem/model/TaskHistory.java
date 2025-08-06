package taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String action; // e.g., "CREATED", "UPDATED", "COMPLETED", "DELETED"
    
    @Column(columnDefinition = "TEXT")
    private String details; // JSON string of the task state at the time of the action
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Pre-persist method to set the timestamp
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
