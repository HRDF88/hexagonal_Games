package com.openclassrooms.hexagonal.games.di

import com.openclassrooms.hexagonal.games.data.repository.CommentRepository
import com.openclassrooms.hexagonal.games.data.repository.ImageRepository
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.data.repositoryInterface.CommentRepositoryInterface
import com.openclassrooms.hexagonal.games.data.repositoryInterface.ImageRepositoryInterface
import com.openclassrooms.hexagonal.games.data.repositoryInterface.PostRepositoryInterface
import com.openclassrooms.hexagonal.games.data.repositoryInterface.UserRepositoryInterface
import com.openclassrooms.hexagonal.games.data.service.firebase.CollectionCommentFireBaseApi
import com.openclassrooms.hexagonal.games.data.service.firebase.CollectionUserFirebaseApi
import com.openclassrooms.hexagonal.games.data.service.firebase.CollectionPostFireBaseApi
import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseImageApi
import com.openclassrooms.hexagonal.games.data.service.firebase.MyFirebaseMessagingService
import com.openclassrooms.hexagonal.games.data.service.serviceInterface.CommentApi
import com.openclassrooms.hexagonal.games.data.service.serviceInterface.PostApi
import com.openclassrooms.hexagonal.games.domain.useCase.UploadImageUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.user.CheckIfEmailExistsUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.user.CreateUserUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.user.DeleteUserUseCase
import com.openclassrooms.hexagonal.games.domain.useCase.user.GetUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
  /**
   * Provides a Singleton instance of PostApi using a PostFakeApi implementation for testing purposes.
   * This means that whenever a dependency on PostApi is requested, the same instance of PostFakeApi will be used
   * throughout the application, ensuring consistent data for testing scenarios.
   *
   * @return A Singleton instance of PostFakeApi.
   */
  @Provides
  @Singleton
  fun providePostApi(): PostApi {
    return CollectionPostFireBaseApi()
  }

  @Provides
  @Singleton
  fun provideCommentApi() : CommentApi{
    return  CollectionCommentFireBaseApi()
  }
  @Provides
  fun provideUserFirebaseApi(): CollectionUserFirebaseApi {
    return CollectionUserFirebaseApi()
  }

  @Provides
  fun provideUserRepository(api: CollectionUserFirebaseApi): UserRepositoryInterface {
    return UserRepository(api)
  }

  @Provides
  fun provideCreateUserUseCase(userRepository: UserRepositoryInterface): CreateUserUseCase {
    return CreateUserUseCase(userRepository)
  }

  @Provides
  fun provideGetUserUseCase(userRepository: UserRepositoryInterface): GetUserUseCase {
    return GetUserUseCase(userRepository)
  }

  @Provides
  fun provideDeleteUserUseCase(userRepository: UserRepositoryInterface): DeleteUserUseCase {
    return DeleteUserUseCase(userRepository)
  }

  @Provides
  fun provideCheckIfEmailExistsUseCase(userRepository: UserRepositoryInterface) : CheckIfEmailExistsUseCase {
    return CheckIfEmailExistsUseCase(userRepository)
  }

  @Provides
  fun provideFirebaseMessagingService(): MyFirebaseMessagingService {
    return MyFirebaseMessagingService()
  }

  @Provides
  @Singleton
  fun providePostRepository(postApi : PostApi): PostRepositoryInterface {
    return PostRepository(postApi)
  }
  @Provides
  @Singleton
  fun provideFirebaseImageApi(): FirebaseImageApi {
    return FirebaseImageApi()
  }

  @Provides
  @Singleton
  fun provideImageRepository(firebaseImageApi: FirebaseImageApi): ImageRepositoryInterface {
    return ImageRepository(firebaseImageApi)
  }

  @Provides
  fun provideUploadImageUseCase(imageRepository: ImageRepositoryInterface): UploadImageUseCase {
    return UploadImageUseCase(imageRepository)
  }

  @Provides
  fun provideCommentRepository(commentApi: CommentApi) : CommentRepositoryInterface{
    return CommentRepository(commentApi)
  }
}
