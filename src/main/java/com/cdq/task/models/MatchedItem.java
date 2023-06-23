package com.cdq.task.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MatchedItem {
    @Column(name = "best_match_index")
    private int bestMatchIndex;
    @Column(name = "best_match_typos")
    private int bestMatchTypos;
}
