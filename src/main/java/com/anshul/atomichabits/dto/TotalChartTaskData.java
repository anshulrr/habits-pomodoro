package com.anshul.atomichabits.dto;

import java.util.List;

public record TotalChartTaskData(List<IndexedTime> dataArr, String entity, String color, int level1, int level2, int level3) {}


