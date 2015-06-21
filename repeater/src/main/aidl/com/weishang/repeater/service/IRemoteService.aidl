package com.weishang.repeater.service;

interface IRemoteService {
	void play();

	void pause();

	void seekTo(int progress);

	boolean next();

	boolean previous();

	int getProgress();

	boolean isPlaying();

	boolean isNewPlay(String path);
	 
 	boolean isPlayComplete();
}
