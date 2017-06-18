package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;
import com.hjwylde.rivers.ui.contracts.EditSectionContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static java.util.Objects.requireNonNull;

public final class EditSectionPresenter implements EditSectionContract.Presenter {
    private final EditSectionContract.View mView;
    private final Repository mRepository;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public EditSectionPresenter(@NonNull EditSectionContract.View view, @NonNull Repository repository) {
        mView = requireNonNull(view);
        mRepository = requireNonNull(repository);
    }


    @Override
    public void createImage(@NonNull Image.Builder builder) {
        Disposable disposable = mRepository.createImage(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onCreateImageSuccess, mView::onCreateImageFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getImage(@NonNull String id) {
        // TODO (hjw): should we care about if the image can't be found? If no, delete View::onGetImageFailure
        Disposable disposable = mRepository.getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> {
                    mView.setImage(image);
                    mView.refreshImage();
                }, mView::onGetImageFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    @Override
    public void updateSection(@NonNull Section.Builder builder) {
        Disposable disposable = mRepository.updateSection(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onUpdateSectionSuccess, mView::onUpdateSectionFailure);

        mDisposables.add(disposable);
    }
}
