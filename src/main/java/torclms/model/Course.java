package torclms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "courses")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="course_id")
    private int courseId;

    @NotBlank
    private String title;

    @Column(name = "title_audio")
    private String titleAudio;

    private String imageName;

    @Column(name = "date_created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date dateCreated;

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date();
    }

    @Column(columnDefinition = "TINYINT DEFAULT 1", nullable = true)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean enabled;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, cascade =  CascadeType.ALL)
    @JsonManagedReference("courseStages")
    @OrderBy("stepOrder ASC")
    Set<Stage> stages = new HashSet<>();

    @OneToMany(mappedBy="assignedCourse", fetch = FetchType.LAZY, cascade =  CascadeType.ALL)
    //@JsonBackReference("courseAssignedUser")
    @JsonIgnore
    private Set<UserAssignment> assignedUsers;

    public Course () {}

    public Course (String title) {
        setTitle(title);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int id) {
        this.courseId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleAudio() {
        return titleAudio;
    }

    public void setTitleAudio(String titleAudio) {
        this.titleAudio = titleAudio;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Stage> getStages() {
        return stages;
    }

    public void setStages(Set<Stage> stages) {
        this.stages = stages;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Set<UserAssignment> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<UserAssignment> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
