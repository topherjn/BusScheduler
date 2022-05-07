/*
 * Copyright (C) 2021 The Android Open Source Project
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
package com.example.busschedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busschedule.databinding.SiteListFragmentBinding
import com.example.busschedule.viewmodels.SiteViewModel
import com.example.busschedule.viewmodels.SiteViewModelFactory
import kotlinx.coroutines.launch

class SiteListFragment: Fragment() {

    private var _binding: SiteListFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private var arrondissement: Int = 0

    private val viewModel: SiteViewModel by activityViewModels {
        SiteViewModelFactory((activity?.application as SiteApplication).database.siteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            arrondissement = it.getInt("arrondissement")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SiteListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        (activity as AppCompatActivity).supportActionBar?.title = "Sites"

        val siteAdapter = SiteAdapter {
            var siteId = it.siteId
            val action = SiteListFragmentDirections.actionFullScheduleFragmentToInsertSiteFragment(siteId)
            findNavController().navigate(action)
        }



        recyclerView.adapter = siteAdapter

        lifecycle.coroutineScope.launch {
            viewModel.getSites(arrondissement).collect {
                siteAdapter.submitList(it)
            }
        }

        val action = SiteListFragmentDirections.actionFullScheduleFragmentToInsertSiteFragment(arrondissement)
        val insertButton = binding.insertButton
        insertButton.setOnClickListener { it.findNavController().navigate(action)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
