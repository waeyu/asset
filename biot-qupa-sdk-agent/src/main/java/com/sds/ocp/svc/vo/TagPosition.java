package com.sds.ocp.svc.vo;

import java.util.List;

public class TagPosition {
	
	private String id;
	
	private String name;
	
	private Long positionTS;
	
	private List<Double> smoothedPosition;
	
	private Double smoothedPositionAccuracy;
	
	private String color;
	
	private List<Zone> zones;
	
	private String coordinateSystemId;
	
	private String coordinateSystemName;
	
	private String areaId;
	
	private String areaName;
	
	private List<Double> position;
	
	private Double positionAccuracy;
	
	private List<Double> covarianceMatrix;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPositionTS() {
		return positionTS;
	}

	public void setPositionTS(Long positionTS) {
		this.positionTS = positionTS;
	}

	public List<Double> getSmoothedPosition() {
		return smoothedPosition;
	}

	public void setSmoothedPosition(List<Double> smoothedPosition) {
		this.smoothedPosition = smoothedPosition;
	}

	public Double getSmoothedPositionAccuracy() {
		return smoothedPositionAccuracy;
	}

	public void setSmoothedPositionAccuracy(Double smoothedPositionAccuracy) {
		this.smoothedPositionAccuracy = smoothedPositionAccuracy;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}

	public String getCoordinateSystemId() {
		return coordinateSystemId;
	}

	public void setCoordinateSystemId(String coordinateSystemId) {
		this.coordinateSystemId = coordinateSystemId;
	}

	public String getCoordinateSystemName() {
		return coordinateSystemName;
	}

	public void setCoordinateSystemName(String coordinateSystemName) {
		this.coordinateSystemName = coordinateSystemName;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<Double> getPosition() {
		return position;
	}

	public void setPosition(List<Double> position) {
		this.position = position;
	}

	public Double getPositionAccuracy() {
		return positionAccuracy;
	}

	public void setPositionAccuracy(Double positionAccuracy) {
		this.positionAccuracy = positionAccuracy;
	}

	public List<Double> getCovarianceMatrix() {
		return covarianceMatrix;
	}

	public void setCovarianceMatrix(List<Double> covarianceMatrix) {
		this.covarianceMatrix = covarianceMatrix;
	}

}
