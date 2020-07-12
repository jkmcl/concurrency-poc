package jkml.client;

public class MyRequestCallback implements RequestCallback {

	private Message response = null;

	private Throwable throwable = null;

	public Message getResponse() {
		return response;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	boolean isSuccess() {
		return throwable == null && response != null;
	}

	boolean isFailure() {
		return throwable != null;
	}

	@Override
	public void onSuccess(Message response) {
		this.response = response;
	}

	@Override
	public void onFailure(Throwable throwable) {
		this.throwable = throwable;
	}

}
