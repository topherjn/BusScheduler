package com.example.busschedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.busschedule.databinding.InsertSiteFragmentBinding
import com.example.busschedule.viewmodels.SiteViewModel
import com.example.busschedule.viewmodels.SiteViewModelFactory

class InsertSiteFragment : Fragment() {

    private var _binding: InsertSiteFragmentBinding? = null

    private val binding get() = _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Insert New Site"

        val arrondissementEdit = binding.arrondissementEditText
        arrondissementEdit.setText(arrondissement.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = InsertSiteFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }
}