package com.moraware.domain.usecase.messages

import com.moraware.domain.interactors.Failure

class SubmitMessageFailure(val timestamp: Long) : Failure.FeatureFailure() {
}