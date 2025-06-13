package com.mitec.business.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "report_pdf")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportPDF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName; // File name of the stored PDF file

    @Column(name = "keyword1")
    private String keyword1; // X-coordinate for text placement

    @Column(name = "index1")
    private int index1; // Y-coordinate for text placement

    @Column(name = "keyword2")
    private String keyword2; // X-coordinate for text placement

    @Column(name = "index2")
    private int index2; // Y-coordinate for text placement

    @Column(name = "keyword3")
    private String keyword3; // X-coordinate for text placement

    @Column(name = "index3")
    private int index3; // Y-coordinate for text placement

    @Column(name = "keyword4")
    private String keyword4; // X-coordinate for text placement

    @Column(name = "index4")
    private int index4; // Y-coordinate for text placement

    @Column(name = "keyword5")
    private String keyword5; // X-coordinate for text placement

    @Column(name = "index5")
    private int index5; // Y-coordinate for text placement


    @Column(name = "keyword6")
    private String keyword6; // X-coordinate for text placement

    @Column(name = "index6")
    private int index6; // Y-coordinate for text placement

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;
}
