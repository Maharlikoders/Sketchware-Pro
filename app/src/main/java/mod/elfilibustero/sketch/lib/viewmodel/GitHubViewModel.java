package mod.elfilibustero.sketch.lib.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GitHubViewModel extends ViewModel {

	private final MutableLiveData<String> status = new MutableLiveData<>();

	public LiveData<String> getStatus(){
		return status;
	}

	public void updateStatus(String _status) {
		status.postValue(_status);
	}
}