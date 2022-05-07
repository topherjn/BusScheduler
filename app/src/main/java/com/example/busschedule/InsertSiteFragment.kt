package com.example.busschedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.busschedule.database.Site
import com.example.busschedule.databinding.InsertSiteFragmentBinding
import com.example.busschedule.viewmodels.SiteViewModel
import com.example.busschedule.viewmodels.SiteViewModelFactory
import kotlinx.coroutines.launch

class InsertSiteFragment : Fragment() {

    private val viewModel: SiteViewModel by activityViewModels {
        SiteViewModelFactory((activity?.application as SiteApplication).database.siteDao())
    }

    private val navigationArgs: InsertSiteFragmentArgs by navArgs()

    private var _binding: InsertSiteFragmentBinding? = null

    private val binding get() = _binding!!

    private var siteId: Int = 0

    private lateinit var site: Site



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            siteId = it.getInt("arrondissement")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InsertSiteFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Site Details"

        val siteId = navigationArgs.arrondissement

        if(siteId > 0) {
            lifecycle.coroutineScope.launch {
                viewModel.getSite(siteId = siteId).collect() {
                    site = it.get(0)
                    bind(site)
                }
            }
        }
    }


    private fun bind(site: Site) {
        binding.apply {
            siteName.setText(site.siteName, TextView.BufferType.SPANNABLE)
            arrondissementEditText.setText(site.arrondissement.toString(), TextView.BufferType.SPANNABLE)
            notes.setText(site.notes, TextView.BufferType.SPANNABLE)
            viewMapButton.setOnClickListener {viewMap(site.url.toString())}
        }
    }

    private fun viewMap(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}