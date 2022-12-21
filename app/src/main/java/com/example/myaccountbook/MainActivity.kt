package com.example.myaccountbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myaccountbook.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

// viewBinding 세팅 & 어댑터 설정
class MainActivity : AppCompatActivity() {

    // 바인딩 객체
    private lateinit var binding : ActivityMainBinding


    // 탭 아이콘 3개 리스트로 만들기
    private val tabIcon = listOf(
        R.drawable.ic_home,
        R.drawable.ic_note,
        R.drawable.ic_challenge
    )

    //git hub commit용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // activity_main 에서 만든 뷰페이져2를 바인딩해서 붙임?
//        binding.viewpager.apply {
//            adapter = MyPagerAdapter(context as FragmentActivity)
//        }
        val pagerAdapter = PagerAdapter(this)
        binding.viewpager.adapter = pagerAdapter
        // 탭레이아웃과 뷰페이져2 연결
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            // tab 객체에 텍스트 표시
            when(position) {
                0 -> tab.text = "예산"
                1 -> tab.text = "기록"
                2 -> tab.text = "챌린지"
            }
            // 각 포지션에 맞게 탭 아이콘 리스트에서 이미지 배치
            tab.setIcon(tabIcon[position])
        }.attach()
    }

    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return 3
        }
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val budgetFragment = BudgetFragment()
                    when {
                        intent.hasExtra("totalbudget") -> {
                            val bundle = intent.getBundleExtra("totalbudget")
                            budgetFragment.arguments = bundle
                        }
                        intent.hasExtra("spendAmount") -> {
                            val bundle = intent.getBundleExtra("spendAmount")
                            budgetFragment.arguments = bundle
                        }
                    }
                    budgetFragment
                }
                1 -> RecordFragment_bc()
                else -> ChallengeFragment()
            }
        }
    }



}