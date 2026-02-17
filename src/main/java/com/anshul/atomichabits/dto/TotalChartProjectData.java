package com.anshul.atomichabits.dto;

import java.util.List;

public record TotalChartProjectData(List<IndexedTime> dataArr, String entity, String color, int level1, int level2) {}


