package com.anshul.atomichabits.dto;

import java.util.List;

public record TotalChartProjectData(List<IndexedTime> dataArr, String color, int level, int priority) {}


