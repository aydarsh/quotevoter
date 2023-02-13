package com.rostertwo.quotevoter.dto;

import com.rostertwo.quotevoter.enums.VoteType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Map;

@Value
@Builder
public class GraphData implements DataTransferObject {

    LocalDate date;
    Map<VoteType, Long> voteCount;
}
