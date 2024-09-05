package fr.vdm.referentiel.refadmin.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseEntity implements Serializable {

    @CreatedDate
    @NotNull
    @Column(name = "TSCREAT", nullable = false, updatable = false)
    private Instant tsCreat;

    @CreatedBy
    @Size(max = 60)
    @NotNull
    @Column(name = "INTERVCREAT", nullable = false, updatable = false, length = 60)
    private String intervCreat = "RFA";


    @NotNull
    @Column(name = "VERSION", columnDefinition = "integer default 0",nullable = false)
    private Integer version = 0;

    @LastModifiedDate
    @NotNull
    @Column(name = "TSMODIF", nullable = false)
    private Instant tsModif;

    @LastModifiedBy
    @Size(max = 60)
    @NotNull
    @Column(name = "INTERVMODIF", nullable = false, length = 60)
    private String intervModif;
}
