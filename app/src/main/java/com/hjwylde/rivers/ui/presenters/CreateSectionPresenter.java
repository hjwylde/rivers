package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static java.util.Objects.requireNonNull;

public final class CreateSectionPresenter implements CreateSectionContract.Presenter {
    private final CreateSectionContract.View mView;
    private final Repository mRepository;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public CreateSectionPresenter(@NonNull CreateSectionContract.View view, @NonNull Repository repository) {
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
    public void createSection(@NonNull Section.Builder builder) {
        Disposable disposable = mRepository.createSection(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onCreateSectionSuccess, mView::onCreateImageFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getImage(@NonNull String id) {
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
}
