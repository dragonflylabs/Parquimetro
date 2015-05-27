package lib.lennken.utils;

public interface OnDBDownload {

	public void onSucessDBDownload();
	public void onErrorDBDownload();
    public void onProgressDownload(long maxProgress, int progress);
}
