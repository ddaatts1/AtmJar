package com.mitec.business.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template", columnDefinition = "TEXT")
    private String template;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Override
    public String toString() {
        return "Report [id=" + id + ", template=" + template + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Report report = (Report) obj;
        return id.equals(report.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
