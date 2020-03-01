package com.kent.university.privelt.model;

import java.io.Serializable;
import java.util.List;

public class Card implements Serializable {
    private String title;
    private boolean watched;
    private boolean isService;
    private List<CardItem> metrics;

    public Card(String title, boolean watched, boolean isService, List<CardItem> metrics) {
        this.title = title;
        this.watched = watched;
        this.isService = isService;
        this.metrics = metrics;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isService() {
        return isService;
    }

    public void setService(boolean service) {
        isService = service;
    }

    public List<CardItem> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<CardItem> metrics) {
        this.metrics = metrics;
    }

    public CardItem getCardItemWithCardIemTitle(String title) {
        for (int i = 0; i < metrics.size(); i++) {
            if (metrics.get(i).getName().equals(title))
                return metrics.get(i);
        }
        return null;
    }
}
