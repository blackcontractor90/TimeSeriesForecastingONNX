# Time-Series Forecasting Framework (JavaFX + ONNX)

A modular, end-to-end time-series forecasting framework built in Java, featuring ONNX-based model inference, automated evaluation, and integrated visualization through a JavaFX desktop interface.

---

## Overview

This project implements a complete forecasting pipeline from raw CSV data ingestion to prediction, evaluation, visualization, and export within a clean, extensible architecture.

It is designed for:
- Research experimentation  
- Model benchmarking  
- Rapid prototyping of forecasting systems  
- Demonstration of ML deployment in Java  

---

## Forecasting Pipeline

Time Series Data
      ->
Sliding Window Transformation
      ->
ONNX Model Inference
      ->
Prediction vs Actual Comparison
      ->
Evaluation Metrics
      ->
Visualization + Export

---

## Key Features

### ONNX-Based Model Inference
- Supports deep learning models such as MLP, LSTM, and Transformer
- Uses precomputed normalization (scaler mean and scale)
- Easily replaceable model backend

### Modular Architecture

| Layer | Responsibility |
|------|--------|
| UI (JavaFX) | User interaction |
| Controller | Pipeline orchestration |
| Services | Core logic |
| Domain | Data structures |
| Visualization | Graph rendering |
| Export | Output handling |

### Automated Evaluation and Visualization
- Prediction versus actual graphs  
- Metrics visualization  
- Graphs automatically saved as images  

### Reproducible Output System
- Timestamp-based outputs:
  forecast_<dataset>_<timestamp>.csv

### Dataset Abstraction
- Supports benchmark datasets such as ETTh1
- Easily extendable

### JavaFX GUI
- One-click forecasting
- Real-time feedback

---

## Getting Started

### Clone
git clone https://github.com/your-username/your-repo.git

### Run
Execute:
TimeSeriesForecastingApp.java

---

## Output

output/
 ├── forecast_<timestamp>.csv
 └── graphs/

---

## Author
blackcontractor90

---

## License
MIT License
