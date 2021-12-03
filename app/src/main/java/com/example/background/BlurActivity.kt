/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.example.background.databinding.ActivityBlurBinding


private const val TAG = "BlurActivity"
class BlurActivity : AppCompatActivity() {

    private val viewModel: BlurViewModel by viewModels {
        BlurViewModel.BlurViewModelFactory(
            application
        )
    }
    private lateinit var binding: ActivityBlurBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.seeFileButton.setOnClickListener {
            viewModel.outputUri?.let {
                val intent = Intent(Intent.ACTION_VIEW, it)
                intent.resolveActivity(packageManager).run {
                    startActivity(intent)
                }
            }
        }
        binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }

        viewModel.outputWorkInfos.observe(this, workInfoObserver())
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer { workInfo ->

            if (workInfo.isNullOrEmpty()) {
                return@Observer
            }
            val outputWorkInfo = workInfo[0]

            if (outputWorkInfo.state.isFinished) {
                showWorkFinished()
                val outputImageUri = outputWorkInfo.outputData.getString(KEY_IMAGE_URI)
                viewModel.setOutputUri(outputImageUri)
                binding.seeFileButton.visibility = View.VISIBLE
            } else {
                showWorkInProgress()
            }

        }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
        }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
    // new line
}
