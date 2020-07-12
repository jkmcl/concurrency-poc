package jkml.client;

public interface RequestCallback {

	void onSuccess(Message response);

	void onFailure(Throwable exception);

}
