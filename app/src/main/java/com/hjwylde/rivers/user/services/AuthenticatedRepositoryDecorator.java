package com.hjwylde.rivers.user.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;
import com.hjwylde.rivers.user.models.Permission;
import com.hjwylde.rivers.user.models.User;

import java.util.List;
import java.util.concurrent.CancellationException;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static java.util.Objects.requireNonNull;

public final class AuthenticatedRepositoryDecorator implements Repository {
    private final Repository mRepository;
    private final User mUser;

    private AuthenticatedRepositoryDecorator(@NonNull Repository repository, @NonNull User user) {
        mRepository = requireNonNull(repository);
        mUser = requireNonNull(user);
    }

    @NonNull
    public static Repository decorate(@NonNull Repository repository, @NonNull User user) {
        if (repository instanceof AuthenticatedRepositoryDecorator) {
            AuthenticatedRepositoryDecorator authenticatedRepository = (AuthenticatedRepositoryDecorator) repository;

            return new AuthenticatedRepositoryDecorator(authenticatedRepository.mRepository, user);
        }

        return new AuthenticatedRepositoryDecorator(repository, user);
    }

    @NonNull
    @Override
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        if (mUser.hasPermission(Permission.Image.CREATE)) {
            return mRepository.createImage(builder);
        }

        return Single.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Single<Section> createSection(@NonNull Section.Builder builder) {
        if (mUser.hasPermission(Permission.Section.CREATE)) {
            return mRepository.createSection(builder);
        }

        return Single.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Completable deleteSection(@NonNull String id) {
        if (mUser.hasPermission(Permission.Section.DELETE)) {
            return mRepository.deleteSection(id);
        }

        return Completable.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Maybe<Image> getImage(@NonNull String id) {
        if (mUser.hasPermission(Permission.Image.READ)) {
            return mRepository.getImage(id);
        }

        return Maybe.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Maybe<Section> getSection(@NonNull String id) {
        if (mUser.hasPermission(Permission.Section.READ)) {
            return mRepository.getSection(id);
        }

        return Maybe.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Observable<Section> getSections() {
        if (mUser.hasPermission(Permission.Section.READ)) {
            return mRepository.getSections();
        }

        return Observable.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Flowable<Section> streamSection(@NonNull String id) {
        if (mUser.hasPermission(Permission.Section.READ)) {
            return mRepository.streamSection(id);
        }

        return Flowable.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Flowable<List<Section>> streamSections() {
        if (mUser.hasPermission(Permission.Section.READ)) {
            return mRepository.streamSections();
        }

        return Flowable.error(new CancellationException("Insufficient permissions"));
    }

    @NonNull
    @Override
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        if (mUser.hasPermission(Permission.Section.UPDATE)) {
            return mRepository.updateSection(builder);
        }

        return Single.error(new CancellationException("Insufficient permissions"));
    }
}
