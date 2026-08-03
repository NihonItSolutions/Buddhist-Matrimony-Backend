package com.matrimony.backend.entity;

import com.matrimony.backend.enums.MasterDataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "master_data", indexes = {
        @Index(name = "idx_master_type", columnList = "type"),
        @Index(name = "idx_master_parent", columnList = "parent_id")
})
public class MasterData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private MasterDataType type;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(nullable = false, length = 160)
    private String code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MasterData parent;
    @Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false)
    private int displayOrder;
}
