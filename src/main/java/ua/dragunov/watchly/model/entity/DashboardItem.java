package ua.dragunov.watchly.model.entity;

public class DashboardItem {
    private Long dashboardId;
    private Long watchlistItemId;
    private int position;

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public long getWatchlistItemId() {
        return watchlistItemId;
    }

    public void setWatchlistItemId(Long watchlistItemId) {
        this.watchlistItemId = watchlistItemId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
